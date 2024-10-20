CREATE TABLE Employee (
    EmployeeID VARCHAR(10) PRIMARY KEY,
    EmployeeName VARCHAR(100),
    PhoneNumber VARCHAR(15),
    EmployeeEmail VARCHAR(100) UNIQUE
);

-- Tạo bảng LoaiDonTu
CREATE TABLE LeaveType (
    LeaveTypeID VARCHAR(10) PRIMARY KEY,
    LeaveTypeName VARCHAR(100)
);

-- Tạo bảng CaChamCong
CREATE TABLE WorkShift (
    ShiftID VARCHAR(10) PRIMARY KEY,
    ShiftName VARCHAR(100),
    StartTime TIME,
    EndTime TIME
);

-- Tạo bảng TaiKhoan
CREATE TABLE Account (
    AccountID VARCHAR(100) PRIMARY KEY,
    Passwordd VARCHAR(255),
    FullName  VARCHAR(100),
    EmployeeEmail VARCHAR(100),
     FOREIGN KEY (EmployeeEmail) REFERENCES Employee(EmployeeEmail)
);
-- Tạo bảng DonTu
CREATE TABLE LeaveRequest (
    RequestID VARCHAR(10) PRIMARY KEY,
    CreatedTime DATETIME,
    Statuss VARCHAR(50),
    LeaveTypeID VARCHAR(10),
    EmployeeEmail  VARCHAR(100),
    PermissionLevel VARCHAR(10),
    FOREIGN KEY (LeaveTypeID) REFERENCES LeaveType(LeaveTypeID),
    FOREIGN KEY (EmployeeEmail) REFERENCES Employee(EmployeeEmail)
);

-- Tạo bảng ChamCong
CREATE TABLE Attendance  (
    AttendanceID VARCHAR(10) PRIMARY KEY,
    CreatedTime DATETIME,
    Statuss VARCHAR(50),
    AttendanceType VARCHAR(50),
    LateTime VARCHAR(25),
    EmployeeID VARCHAR(10),
    ShiftID  VARCHAR(10),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (ShiftID) REFERENCES WorkShift(ShiftID)
);


-- Insert dữ liệu vào bảng NhanVien
INSERT INTO Employee (EmployeeID, EmployeeName, PhoneNumber, EmployeeEmail) VALUES
('NV001', 'Nguyễn Văn Anh', '0901234567', 'nva@example.com'),
('NV002', 'Lê Thị Bình', '0902345678', 'ltb@example.com'),
('NV003', 'Trần Văn Cương', '0903456789', 'tvc@example.com'),
('NV004', 'Phạm Thị Dung', '0904567890', 'ptd@example.com'),
('NV005', 'Hoàng Văn Anh', '0905678901', 'hve@example.com'),
('NV006', 'Nguyễn Thị Lê', '0906789012', 'ntf@example.com'),
('NV007', 'Lê Văn Giang', '0907890123', 'lvg@example.com'),
('NV008', 'Trần Thị Hương', '0908901234', 'tth@example.com'),
('NV009', 'Phạm Văn Anh', '0909012345', 'pvi@example.com'),
('NV010', 'Hoàng Thị Kiên', '0910123456', 'htk@example.com');

-- Insert dữ liệu vào bảng TaiKhoan
INSERT INTO Account (AccountID, Passwordd, FullName,  EmployeeEmail) VALUES
('TK001', 'password1', 'Nguyễn Văn Anh', 'nva@example.com'),
('TK002', 'password2', 'Lê Thị Bình', 'ltb@example.com'),
('TK003', 'password3', 'Trần Văn Cương', 'tvc@example.com'),
('TK004', 'password4', 'Phạm Thị Dung', 'ptd@example.com'),
('TK005', 'password5', 'Hoàng Văn Anh', 'hve@example.com'),
('TK006', 'password6', 'Nguyễn Thị Lê', 'ntf@example.com'),
('TK007', 'password7', 'Lê Văn Giang', 'lvg@example.com'),
('TK008', 'password8', 'Trần Thị Hương', 'tth@example.com'),
('TK009', 'password9', 'Phạm Văn Anh', 'pvi@example.com'),
('TK010', 'password10', 'Hoàng Thị Kiên', 'htk@example.com');

-- Insert dữ liệu vào bảng LoaiDonTu
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

-- Insert dữ liệu vào bảng CaChamCong
INSERT INTO WorkShift (ShiftID, ShiftName, StartTime, EndTime ) VALUES
('CA001', 'Morning shift', '08:00:00', '12:00:00'),
('CA002', 'Afternoon shift', '13:00:00', '17:00:00'),
('CA003', 'Evening Shift', '18:00:00', '22:00:00');

-- Insert dữ liệu vào bảng DonTu
INSERT INTO LeaveRequest (RequestID, CreatedTime, Statuss, LeaveTypeID, EmployeeEmail, PermissionLevel) VALUES
('DT001', '2024-01-01 08:00:00', 'Accept', 'LDT003', 'nva@example.com', 'NV003'),
('DT002', '2024-01-05 08:00:00', 'Accept', 'LDT005', 'ltb@example.com', 'NV002'),
('DT003', '2024-01-10 08:00:00', 'Accept', 'LDT001', 'tvc@example.com', 'NV001'),
('DT004', '2024-01-15 08:00:00', 'Pending approval', 'LDT006', 'ptd@example.com', 'NV005'),
('DT005', '2024-01-20 08:00:00', 'Refused', 'LDT004', 'hve@example.com', 'NV003'),
('DT006', '2024-01-25 08:00:00', 'Accept', 'LDT002', 'ntf@example.com', 'NV002'),
('DT007', '2024-02-01 08:00:00', 'Pending approval', 'LDT003', 'lvg@example.com', 'NV001'),
('DT008', '2024-02-05 08:00:00', 'Refused', 'LDT005', 'tth@example.com', 'NV006'),
('DT009', '2024-02-10 08:00:00', 'Accept', 'LDT001', 'pvi@example.com', 'NV002'),
('DT010', '2024-02-15 08:00:00', 'Pending approval', 'LDT007', 'htk@example.com', 'NV007');

-- Insert dữ liệu vào bảng ChamCong
INSERT INTO Attendance  (AttendanceID, CreatedTime, Statuss, AttendanceType, LateTime, EmployeeID,ShiftID) VALUES
('CC001', '2024-01-01 08:00:00', 'Complete', 'Check in', ' ', 'NV001', 'CA001'),
('CC002', '2024-01-05 13:15:00', 'Complete', 'Check in', '15 minutes late', 'NV002', 'CA002'),
('CC003', '2024-01-10 07:30:00', 'Complete', 'Check in', '', 'NV003', 'CA001'),
('CC004', '2024-01-10 12:30:00', 'Complete', 'Check out', '', 'NV003', 'CA001'),
('CC005', '2024-01-15 12:05:00', 'Complete', 'Check out','5 minutes late ', 'NV004', 'CA001'),
('CC006', '2024-01-20 17:05:00', 'Complete', 'Check out', '5 minutes late', 'NV005', 'CA002'),
('CC007', '2024-01-25 18:05:00', 'Complete', 'Check in', '15 minutes late', 'NV006', 'CA003'),
('CC008', '2024-02-01 08:20:00', 'Complete', 'Check in', '20 minutes late', 'NV007', 'CA001'),
('CC009', '2024-02-05 17:10:00', 'Complete', 'Check out', '10 minutes late', 'NV008', 'CA002'),
('CC0010', '2024-02-10 18:35:00', 'Complete', 'Check in', '35 minutes late', 'NV009', 'CA003'),
('CC011', '2024-02-15 08:50:00', 'Complete', 'Check in', '15 minutes late', 'NV010', 'CA001');