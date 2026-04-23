-- Complex Queries For AmusmentParkDB

-- 1)
-- Returns customerID, full name and balance of customer who is a subscriber with the highest balance but less than 500 points
SELECT cus.CustomerID, CONCAT(cus.First_Name, " ", cus.Last_Name) as Full_Name, ca.balance
FROM customer cus
INNER JOIN Card ca USING (CustomerID)
WHERE cus.type = "Subscription" 
AND ca.balance = (SELECT MAX(balance) 
					FROM Card ca
                    WHERE ca.points < 500);
	
    
-- Query with a view
-- 2)
-- View that returns info of employees who are not managers
CREATE VIEW NonManagers AS
SELECT StaffID, CONCAT(First_Name, " ", Last_Name) AS Name, Title, salary
FROM Staff
WHERE Title NOT LIKE  "%Manager";

-- Return info of staff members working at Haunted House, Mini Train and thier ride cost
SELECT nm.Name, nm.Title, nm.salary, r.Ride_Name, cp.Amount
FROM NonManagers nm
INNER JOIN Ride r ON r.OperatorID = nm.StaffID
INNER JOIN Card_Payment cp USING (RideID)
WHERE Ride_Name = "Haunted House" OR Ride_Name = "Mini Train";


-- 3)
-- View to find only ride operators specifically
CREATE VIEW Operators AS
SELECT s.StaffID, CONCAT (s.First_Name, ' ', s.Last_Name) AS 'Name', s.Title
FROM Staff s
WHERE Title LIKE '%Operator'; 

-- Query which returns ride details Ridden more than once and generated more than 500
-- and the subsequent ride operators 
SELECT r.RideID, r.Ride_Name, sum(cp.Amount) AS Total_Revenue, count(*) AS 'Usage', o.Name, o.Title
FROM Ride r
INNER JOIN Operators o ON o.StaffID = r.OperatorID 
INNER JOIN Card_Payment cp USING (RideID)
GROUP BY r.RideID
HAVING count(*) > 1 AND sum(cp.Amount) > 500;


-- 4)
-- A view to find food stalls of fast food type
DROP VIEW fastFoodStall;		-- To drop the view and change it

CREATE VIEW fastFoodStall AS
SELECT fs.Food_StallID, fs.Food_OwnerID, fs.Name, fs.Rent AS Stall_Rent, fs.Type AS Stall_Type, fs.Establish_Date
FROM food_stalls fs
WHERE fs.Type = 'Fast Food';

-- Query which generates the fast-food Stall revenue and Rent revenue from that stall alongwith its owner details
SELECT ffs.Name AS Stall_Name, 
		ffs.Stall_Type, CONCAT(fo.First_Name, " " , fo.Last_Name) AS Owner_Name, fo.Phone AS Owner_ContactInfo,
		TIMESTAMPDIFF(Month, ffs.Establish_Date, CURRENT_TIMESTAMP) AS Months_Since_Established,
        ffs.Stall_Rent AS Rent_Monthly_Rate,
        ffs.Stall_Rent * TIMESTAMPDIFF(Month, ffs.Establish_Date, CURRENT_TIMESTAMP) AS Total_Rent,
        SUM(FP.Amount) AS Stall_Revenue
FROM fastFoodStall ffs
INNER JOIN food_owner fo USING (Food_OwnerID)
INNER JOIN food_payment fp USING (Food_StallID)
GROUP BY Stall_Name,
		Stall_Type,
		Owner_Name,
        Owner_ContactInfo,
		ffs.Establish_Date,
		Rent_Monthly_Rate
		
ORDER BY Stall_Revenue;

-- 5) Food Stall that generated has the maximum profit margin along with it's type and  Owner details
	SELECT concat(fo.First_Name," ", fo.Last_Name) AS Stall_Owner_Name,
       fs.Type AS Stall_Type,
       ROUND(((SUM(fp.Amount) - fs.Rent) / SUM(fp.Amount)) * 100 , 2 ) AS Profit_Margin
FROM Food_Stalls fs
JOIN Food_Payment fp ON fs.Food_StallID = fp.Food_StallID
JOIN food_owner fo ON fo.Food_OwnerID =fs.Food_OwnerID
GROUP BY fs.Food_StallID, concat(fo.First_Name," ", fo.Last_Name)
ORDER BY Profit_Margin DESC LIMIT 1;

-- 6)
-- A view that lists customers total spending across bowling, cinema and rides
	CREATE VIEW Customer_Spending AS
SELECT 
    c.CustomerID as CustomerID,
    CONCAT(c.First_Name, ' ', c.Last_Name) AS Name,
    SUM(IFNULL(cp.Amount,0) + IFNULL(t.Amount,0) + IFNULL(b.Amount,0)) AS Total_Spending
FROM Customer c
JOIN Card cd ON c.CustomerID = cd.CustomerID
LEFT JOIN Card_Payment cp ON cd.CardID = cp.CardID
LEFT JOIN Ticketing t ON cd.CardID = t.CardID
LEFT JOIN Bowling_Booking b ON cd.CardID = b.CardID
GROUP BY c.CustomerID; 

-- Query which generates comparison  between which Customer Type brings more revenue
 SELECT 
    Customer_Type,
    SUM(Total_Spending) AS Total_Revenue,
    COUNT(CustomerID) AS Total_Customers,
    ROUND(AVG(Total_Spending), 2) AS Avg_Spending
FROM Customer_Spending
GROUP BY Customer_Type;

-- 7)
-- A Query that returns Peak Hour at Cinema and Tickets Sold across all Halls
	SELECT HOUR(sc.Screening_time) AS Peak_Hour,
			COUNT(t.TicketID) AS Tickets_Sold
FROM Screening sc JOIN Ticketing t 
ON sc.ScreeningID = t.ScreeningID
GROUP BY HOUR(sc.Screening_time)
ORDER BY count(t.TicketID) DESC
LIMIT 1;

--8)
-- Top 5 Movie Screening That Generated The highest revenue during a specific month such as 2024 march
	SELECT 
    m.Title as Title,
    COUNT(t.TicketID) AS Tickets_Sold,
    SUM(t.Amount) AS Revenue
FROM Movie m
JOIN Screening sc ON m.MovieID = sc.MovieID
JOIN Ticketing t ON sc.ScreeningID = t.ScreeningID
WHERE DATE_FORMAT(sc.Screening_Time, '%Y-%m') = '2024-03'
GROUP BY m.MovieID, m.Title
ORDER BY Revenue DESC
LIMIT 5;

