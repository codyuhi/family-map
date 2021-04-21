DELETE FROM Users;
DELETE FROM Persons;
DELETE FROM Events;
DELETE FROM AuthorizationTokens;
INSERT INTO Users
    (UserId,UserName,PassWord,Email,FirstName,LastName,Gender,PersonId)
VALUES
    ("admin", "admin", "admin", "admin", "admin", "admin", "m", "admin");