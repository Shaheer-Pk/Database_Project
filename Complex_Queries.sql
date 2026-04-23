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