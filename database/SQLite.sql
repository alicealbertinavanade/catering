SELECT COUNT(*) as count FROM Assignments INNER JOIN Shifts ON Assignments.shift_id = Shifts.id WHERE Assignments.user_id = 11 AND Shifts.date >= '2025-06-14' AND Shifts.date <= '2025-06-17'
