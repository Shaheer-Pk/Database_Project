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


-- 5)
-- Description:
-- Calculates the total revenue of the entire park excluding revenue of the food stalls

-- Card payments
-- tickets
-- Food stalls rent
-- Bowling booking

SELECT ROUND(
	( -- Food stall rent income
		SELECT SUM(Rent * TIMESTAMPDIFF(MONTH, Establish_Date, CURRENT_DATE()))
		FROM Food_Stalls
	) + 
    ( -- Movie income 
		SELECT SUM(Amount)
		FROM Ticketing
    ) +
    ( -- Ride income
		SELECT SUM(Amount)
		FROM Card_Payment
    ) +
    ( -- Bowling income
		SELECT SUM(Amount)
        FROM Bowling_Booking
    )
, 2) AS Total_Income;

-- 6)
-- Description:
-- Find what module has each customer spent most money on


DROP VIEW IF EXISTS Module_Spending;

-- Store each module spending for every customer in this view for later
CREATE VIEW Module_Spending AS
SELECT
	CustomerID,
	CONCAT(Customer.First_Name, " ", Customer.Last_Name) AS Full_Name,
    SUM(Card_Payment.Amount) AS Ride_Spendings,
    SUM(Ticketing.Amount) AS Movie_Spendings,
    SUM(Bowling_Booking.Amount) AS Bowling_Spendings
FROM Customer 
INNER JOIN Card USING (CustomerID)
INNER JOIN Card_Payment USING (CardID)
INNER JOIN Ticketing USING (CardID)
INNER JOIN Bowling_Booking USING (CardID)
GROUP BY CustomerID;

-- Find what module did each customer spend on the most
SELECT
	CustomerID,
    Full_Name,
    (
		CASE
			WHEN Ride_Spendings > Movie_Spendings AND Ride_Spendings > Bowling_Spendings THEN "Rides"
            WHEN Movie_Spendings > Bowling_Spendings THEN "Movies"
            ELSE "Bowling"
		END
    ) AS Module_Spent_Most_On
FROM Module_Spending;

-- 7 Compare Food Stall Rent to Food Stall Revenue form most recent month, then suggest whether rent should be increased or decreased
SELECT fs.Food_StallID,fs.Name as Name,SUM(Amount) as Revenue,Rent,
		(	
			CASE 
				WHEN SUM(Amount) > fs.Rent * 1.5 THEN "Increase"
                WHEN SUM(Amount) < fs.Rent * 1.5 THEN "Decrease"
                ELSE "Dont Change"
		END
        ) AS Suggestion
FROM Food_Stalls as fs
INNER JOIN Food_Payment as fp USING (Food_StallID)

WHERE (MONTH(fp.Payment_Time),Year(fp.Payment_Time)) =
							(
								SELECT MAX(MONTH(Payment_Time)),MAX(YEAR(Payment_Time)) FROM Food_Payment as fp2
								WHERE fp.Food_StallID = fp2.Food_StallID
							)

GROUP BY fp.Food_StallID,MONTH(fp.Payment_Time), YEAR(fp.Payment_Time);
	
-- 8- Stored Procedure- Give Customers who are registered but did not make any transactions-- Use for Marketing ✌

DROP PROCEDURE GetCustomersWithNoTransactions;
DELIMITER //
CREATE PROCEDURE GetCustomersWithNoTransactions()
BEGIN
SELECT c.CustomerID , CONCAT(c.First_Name, " ", c.Last_Name) AS "Full Name" FROM Customer c
LEFT JOIN Card ca ON ca.CustomerID = c.CustomerID
LEFT JOIN Card_Payment cp ON ca.CardID = cp.CardID
LEFT JOIN Ticketing t ON ca.CardID = t.CardID
LEFT JOIN Bowling_Booking b ON ca.CardID = b.CardID
GROUP BY CustomerID, c.First_Name,c.Last_Name

Having SUM(cp.Amount) IS Null
AND SUM(t.Amount) IS Null
AND SUM(b.Amount) IS Null ;

END //
DELIMITER ;

Call GetCustomersWithNoTransactions();