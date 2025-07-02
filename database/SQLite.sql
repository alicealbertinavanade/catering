-- SQLite
SELECT DISTINCT ShiftBookings.* FROM ShiftBookings 
INNER JOIN Users ON Users.id = ShiftBookings.user_id 
INNER JOIN Shifts ON ShiftBookings.shift_id = Shifts.id 
INNER JOIN UserRoles ON Users.id = UserRoles.user_id 
WHERE Users.is_occasional_user = 0 AND Shifts.date = '2025-06-15'