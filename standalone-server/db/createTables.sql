CREATE TABLE
IF NOT EXISTS Users
(
    UserID TEXT PRIMARY KEY NOT NULL UNIQUE,
    UserName TEXT NOT NULL UNIQUE,
    PassWord TEXT NOT NULL,
    Email TEXT NOT NULL UNIQUE,
    FirstName TEXT NOT NULL,
    LastName TEXT NOT NULL,
    Gender TEXT CHECK
(Gender IN
('f','m')) NOT NULL,
    PersonID TEXT NOT NULL UNIQUE
);

CREATE TABLE
IF NOT EXISTS AuthorizationTokens
(
    TokenID TEXT PRIMARY KEY NOT NULL UNIQUE,
    AuthKey TEXT NOT NULL UNIQUE,
    UserID TEXT NOT NULL
);

CREATE TABLE
IF NOT EXISTS Persons
(
    PersonID TEXT PRIMARY KEY NOT NULL UNIQUE,
    AssociatedUserName TEXT,
    FirstName TEXT NOT NULL,
    LastName TEXT NOT NULL,
    Gender TEXT CHECK
(Gender IN
('f','m')) NOT NULL,
    FatherID TEXT,
    MotherID TEXT,
    SpouseID TEXT,
    AssociatedUserID TEXT
);

CREATE TABLE
IF NOT EXISTS Events
(
    EventID TEXT PRIMARY KEY NOT NULL UNIQUE,
    PersonID TEXT NOT NULL,
    AssociatedUserName TEXT,
    Latitude NUMBER,
    Longitude NUMBER,
    Country TEXT,
    City TEXT,
    EventType TEXT,
    Year INTEGER
);