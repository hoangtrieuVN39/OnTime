
-- Tạo cơ sở dữ liệu
CREATE DATABASE checkin;
USE checkin;

-- Bảng Place
CREATE TABLE Place (
    PlaceID VARCHAR(10) PRIMARY KEY,
    PlaceName VARCHAR(100),
    Latitude DECIMAL(9, 6), -- Sửa độ chính xác
    Longitude DECIMAL(9, 6) -- Sửa độ chính xác
);

-- Bảng WorkShift
CREATE TABLE WorkShift (
    ShiftID VARCHAR(10) PRIMARY KEY,
    ShiftName VARCHAR(50),
    StartTime TIME,
    EndTime TIME
);

-- Bảng Employee
CREATE TABLE Employee (
    EmployeeID VARCHAR(10) PRIMARY KEY,
    EmployeeName VARCHAR(100),
    Phone VARCHAR(15),
    Email VARCHAR(100)
);

-- Bảng Account
CREATE TABLE Account (
    AccountID VARCHAR(10) PRIMARY KEY,
    Passwordd VARCHAR(255),
    Email VARCHAR(100),
    EmployeeID VARCHAR(10),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Bảng LeaveType
CREATE TABLE LeaveType (
    LeaveTypeID VARCHAR(10) PRIMARY KEY,
    LeaveTypeName VARCHAR(100)
);

-- Bảng LeaveRequest
CREATE TABLE LeaveRequest (
    LeaveID VARCHAR(10) PRIMARY KEY,
    CreatedTime DATETIME,
    Statuss VARCHAR(50),
    LeaveTypeID VARCHAR(10),
    EmployeeID VARCHAR(10),
    LeaveStartTime DATETIME,
    LeaveEndTime DATETIME,
    Reason TEXT,
    FOREIGN KEY (LeaveTypeID) REFERENCES LeaveType(LeaveTypeID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Bảng Attendance
CREATE TABLE Attendance (
    AttendanceID VARCHAR(10) PRIMARY KEY,
    CreatedTime DATETIME,
    AttendanceType VARCHAR(50),
    EmployeeID VARCHAR(10),
    ShiftID VARCHAR(10),
    PlaceID VARCHAR(10),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (ShiftID) REFERENCES WorkShift(ShiftID),
    FOREIGN KEY (PlaceID) REFERENCES Place(PlaceID)
);

-- Bảng LeaveRequestApproval
CREATE TABLE LeaveRequestApproval (
    LeaveApprovalID VARCHAR(10) PRIMARY KEY,
    LeaveID VARCHAR(10),
    EmployeeID VARCHAR(10),
    Statuss VARCHAR(50),
    FOREIGN KEY (LeaveID) REFERENCES LeaveRequest(LeaveID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Thêm dữ liệu mẫu cho bảng Place
INSERT INTO Place (PlaceID, PlaceName, Latitude, Longitude) VALUES
('VT001', 'Myhome', 10.762622, 106.682699),
('VT002', 'Branch 1', 10.823099, 106.629664),
('VT003', 'Branch 2', 10.762622, 106.629664),
('VT004', 'Warehouse', 10.802622, 106.640172),
('VT005', 'Remote Site 1', 10.712622, 106.670172),
('VT006', 'Remote Site 2', 10.682622, 106.650172),
('VT007', 'Client Office 1', 10.742622, 106.700172),
('VT008', 'Client Office 2', 10.722622, 106.720172),
('VT009', 'Backup Office', 10.762000, 106.750172),
('VT010', 'Test Site', 10.702622, 106.760172); 

-- Thêm dữ liệu mẫu cho bảng WorkShift
INSERT INTO WorkShift (ShiftID, ShiftName, StartTime, EndTime) VALUES
('CA001', 'Ca sang', '08:00:00', '12:00:00'),
('CA002', 'Ca chieu', '13:00:00', '17:00:00'),
('CA003', 'Ca toi', '18:00:00', '22:00:00');

-- Thêm dữ liệu mẫu cho bảng Employee
INSERT INTO Employee (EmployeeID, EmployeeName, Phone, Email) VALUES
('NV001', 'Nguyen Van Anh', '0901234567', 'nva@example.com'),
('NV002', 'Le Thi Binh', '0902345678', 'ltb@example.com'),
('NV003', 'Tran Van Cuong', '0903456789', 'tvc@example.com'),
('NV004', 'Pham Thi Dung', '0904567890', 'ptd@example.com'),
('NV005', 'Hoang Van Anh', '0905678901', 'hve@example.com'),
('NV006', 'Nguyen Thi Le', '0906789012', 'ntf@example.com'),
('NV007', 'Le Van Giang', '0907890123', 'lvg@example.com'),
('NV008', 'Tran Thi Huong', '0908901234', 'tth@example.com'),
('NV009', 'Pham Van Anh', '0909012345', 'pvi@example.com'),
('NV010', 'Hoang Thi Kien', '0910123456', 'htk@example.com');

-- Thêm dữ liệu mẫu cho bảng Account
INSERT INTO Account (AccountID, Passwordd, Email, EmployeeID) VALUES
('TK001', 'password1','nva@example.com','NV001'),
('TK002', 'password2','ltb@example.com','NV002'),
('TK003', 'password3','tvc@example.com','NV003'),
('TK004', 'password4','ptd@example.com','NV004'),
('TK005', 'password5','hve@example.com','NV005'),
('TK006', 'password6','ntf@example.com','NV006'),
('TK007', 'password7','lvg@example.com','NV007'),
('TK008', 'password8','tth@example.com','NV008'),
('TK009', 'password9','pvi@example.com','NV009'),
('TK010', 'password10','htk@example.com','NV010');

-- Thêm dữ liệu mẫu cho bảng LeaveType
INSERT INTO LeaveType (LeaveTypeID, LeaveTypeName) VALUES
('LDT001', 'Late/Early Leave (within 1 hour)'),
('LDT002', 'Unpaid Leave'),
('LDT003', 'Leave - Submit 24 hours in advance'),
('LDT004', 'Wedding/Funeral'),
('LDT005', 'Business Trip'),
('LDT006', 'Work remotely'),
('LDT007', 'Public explanation'),
('LDT008', 'Paid Leave'),
('LDT009', 'Sick Leave'),
('LDT010', 'Maternity Leave');

-- Thêm dữ liệu mẫu cho bảng LeaveRequest
INSERT INTO LeaveRequest (LeaveID, CreatedTime, Statuss, LeaveTypeID, EmployeeID, LeaveStartTime, LeaveEndTime, Reason) VALUES
('DT001', '2024-01-01 08:00:00', 'Approved', 'LDT001', 'NV001', '2024-09-02 08:00:00', '2024-09-05 18:00:00', 'Medical reasons'),
('DT002', '2024-09-10 10:00:00', 'Pending', 'LDT002', 'NV002', '2024-09-11 08:00:00', '2024-09-12 18:00:00', 'Family emergency'),
('DT003', '2024-09-15 11:00:00', 'Approved', 'LDT003', 'NV003', '2024-09-16 08:00:00', '2024-09-30 18:00:00', 'Maternity leave'),
('DT004', '2024-09-20 12:00:00', 'Rejected', 'LDT004', 'NV004', '2024-09-25 08:00:00', '2024-09-26 18:00:00', 'No valid reason'),
('DT005', '2024-10-01 13:00:00', 'Pending', 'LDT005', 'NV005', '2024-10-02 08:00:00', '2024-10-03 18:00:00', 'Business trip'),
('DT006', '2024-10-05 14:00:00', 'Approved', 'LDT006', 'NV006', '2024-10-10 08:00:00', '2024-10-20 18:00:00', 'Vacation leave'),
('DT007', '2024-10-08 15:00:00', 'Pending', 'LDT007', 'NV007', '2024-10-11 08:00:00', '2024-10-12 18:00:00', 'Explanation needed'),
('DT008', '2024-10-10 16:00:00', 'Approved', 'LDT008', 'NV008', '2024-10-15 08:00:00', '2024-10-16 18:00:00', 'Paid leave'),
('DT009', '2024-10-12 17:00:00', 'Rejected', 'LDT009', 'NV009', '2024-10-13 08:00:00', '2024-10-14 18:00:00', 'Sick leave without notice'),
('DT010', '2024-10-14 18:00:00', 'Approved', 'LDT010', 'NV010', '2024-10-20 08:00:00', '2024-10-30 18:00:00', 'Maternity leave');

-- Thêm dữ liệu mẫu cho bảng Attendance
INSERT INTO Attendance (AttendanceID, CreatedTime, AttendanceType, EmployeeID, ShiftID, PlaceID) VALUES
('CC001', '2024-01-01 08:00:00', 'Check in', 'NV001', 'CA001', 'VT001'),
('CC002', '2024-01-01 12:00:00', 'Check out', 'NV001', 'CA001', 'VT001'),
('CC003', '2024-01-01 08:00:00', 'Check in', 'NV002', 'CA001', 'VT002'),
('CC004', '2024-01-01 12:00:00', 'Check out', 'NV002', 'CA001', 'VT002'),
('CC005', '2024-01-01 08:00:00', 'Check in', 'NV003', 'CA001', 'VT003'),
('CC006', '2024-01-01 12:00:00', 'Check out', 'NV003', 'CA001', 'VT003'),
('CC007', '2024-01-01 13:00:00', 'Check in', 'NV004', 'CA002', 'VT004'),
('CC008', '2024-01-01 17:00:00', 'Check out', 'NV004', 'CA002', 'VT004'),
('CC009', '2024-01-01 13:00:00', 'Check in', 'NV005', 'CA002', 'VT005'),
('CC010', '2024-01-01 17:00:00', 'Check out', 'NV005', 'CA002', 'VT005');
