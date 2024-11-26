-- Bảng Place
CREATE TABLE Place (
    PlaceID VARCHAR(10) PRIMARY KEY,
	PlaceName varchar(50),
    Latitude DECIMAL(9, 6), 
    Longitude DECIMAL(9, 6)
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
    Status VARCHAR(50),
    LeaveTypeID VARCHAR(10),
    EmployeeID VARCHAR(10),
    LeaveStartTime DATETIME,
    LeaveEndTime DATETIME,
    Reason TEXT,
	CountShift int,
    FOREIGN KEY (LeaveTypeID) REFERENCES LeaveType(LeaveTypeID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Bảng Attendance
CREATE TABLE Attendance (
    AttendanceID VARCHAR(7) PRIMARY KEY,
    CreatedTime DATETIME,
    AttendanceType VARCHAR(50),
    EmployeeID VARCHAR(10),
    ShiftID VARCHAR(10),
    PlaceID VARCHAR(10),
    Latitude DECIMAL(9, 6), 
    Longitude DECIMAL(9, 6), 
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (ShiftID) REFERENCES WorkShift(ShiftID),
    FOREIGN KEY (PlaceID) REFERENCES Place(PlaceID)
);

-- Bảng LeaveRequestApproval
CREATE TABLE LeaveRequestApproval (
    LeaveApprovalID VARCHAR(10) PRIMARY KEY,
    LeaveID VARCHAR(10),
    EmployeeID VARCHAR(10),
    Status VARCHAR(50),
    FOREIGN KEY (LeaveID) REFERENCES LeaveRequest(LeaveID),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

-- Thêm dữ liệu mẫu cho bảng Place
INSERT INTO Place (PlaceID,PlaceName,Latitude, Longitude) VALUES
('VT001','DHKT', 16.04590, 108.24151),
('VT002','Phòng họp', 16.04821, 108.23918);


-- Thêm dữ liệu mẫu cho bảng WorkShift
INSERT INTO WorkShift (ShiftID, ShiftName, StartTime, EndTime) VALUES
('CA001', 'Ca sáng', '08:00:00', '12:00:00'),
('CA002', 'Ca chiều', '13:00:00', '17:00:00'),
('CA003', 'Ca tối', '18:00:00', '22:00:00');


-- Thêm dữ liệu mẫu cho bảng Employee
INSERT INTO Employee (EmployeeID, EmployeeName, Phone, Email) VALUES
('NV001', 'Nguyễn Văn Anh', '0901234567', 'nva@example.com'),
('NV002', 'Lê Thị Bình', '0902345678', 'ltb@example.com'),
('NV003', 'Trần Văn Cường', '0903456789', 'tvc@example.com'),
('NV004', 'Phạm Thị Dung', '0904567890', 'ptd@example.com'),
('NV005', 'Hoàng Văn Anh', '0905678901', 'hve@example.com'),
('NV006', 'Nguyễn Thị Lê', '0906789012', 'ntf@example.com'),
('NV007', 'Lê Văn Giang', '0907890123', 'lvg@example.com'),
('NV008', 'Trần Thị Hương', '0908901234', 'tth@example.com'),
('NV009', 'Phạm Văn Anh', '0909012345', 'pvi@example.com'),
('NV010', 'Hoàng Thị Kiên', '0910123456', 'htk@example.com');

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
('LDT001', 'Đi trễ/ về sớm (trong vòng 1h)'),
('LDT002', 'Nghỉ không lương'),
('LDT003', 'Nghỉ phép - gửi trước 24h'),
('LDT004', 'Cưới/tang'),
('LDT005', 'Công tác'),
('LDT006', 'Làm việc từ xa'),
('LDT007', 'Giải trình công'),
('LDT008', 'Nghỉ phép có lương'),
('LDT009', 'Nghỉ phép ốm'),
('LDT010', 'Nghỉ thai sản');

-- Thêm dữ liệu mẫu cho bảng LeaveRequest
INSERT INTO LeaveRequest (LeaveID, CreatedTime, Status, LeaveTypeID, EmployeeID, LeaveStartTime, LeaveEndTime, Reason, CountShift) VALUES
('DT001', '2024-01-01 08:00:00', 'Đồng ý', 'LDT001', 'NV001', '2024-09-02 08:00:00', '2024-09-05 18:00:00', 'Lý do y tế', 12),
('DT002', '2024-09-10 10:00:00', 'Chưa phê duyệt', 'LDT002', 'NV002', '2024-09-11 08:00:00', '2024-09-12 18:00:00', 'Gia đình khẩn cấp', 6),
('DT003', '2024-09-15 11:00:00', 'Đồng ý', 'LDT003', 'NV003', '2024-09-16 08:00:00', '2025-02-28 18:00:00', 'Nghỉ thai sản', 1026),
('DT004', '2024-09-20 12:00:00', 'Loại bỏ', 'LDT004', 'NV004', '2024-09-25 08:00:00', '2024-09-26 18:00:00', '', 6),
('DT005', '2024-10-01 13:00:00', 'Chưa phê duyệt', 'LDT005', 'NV005', '2024-10-02 08:00:00', '2024-10-03 18:00:00', 'Chuyến công tác', 6),
('DT006', '2024-10-05 14:00:00', 'Đồng ý', 'LDT006', 'NV006', '2024-10-10 08:00:00', '2024-10-20 18:00:00', 'Nghỉ phép', 33),
('DT007', '2024-10-08 15:00:00', 'Chưa phê duyệt', 'LDT007', 'NV007', '2024-10-11 08:00:00', '2024-10-12 18:00:00', '', 6),
('DT008', '2024-10-10 16:00:00', 'Đồng ý', 'LDT008', 'NV008', '2024-10-15 08:00:00', '2024-10-20 18:00:00', 'Nghỉ phép', 18),
('DT009', '2024-10-15 17:00:00', 'Chưa phê duyệt', 'LDT009', 'NV009', '2024-10-16 08:00:00', '2024-10-20 18:00:00', '', 15),
('DT010', '2024-10-20 18:00:00', 'Đồng ý', 'LDT010', 'NV010', '2024-10-21 08:00:00', '2024-10-24 18:00:00', 'Nghỉ phép', 12);

-- Thêm dữ liệu mẫu cho bảng Attendance
INSERT INTO Attendance (AttendanceID, CreatedTime, AttendanceType, EmployeeID, ShiftID, PlaceID, Latitude, Longitude) VALUES
('AT001', '2024-10-01 08:00:00', 'checkin', 'NV001', 'CA001', 'VT001',  16.04590, 108.24151),
('AT002', '2024-10-01 12:00:00', 'checkout', 'NV001', 'CA001', 'VT001', 16.04590, 108.24151),
('AT003', '2024-10-01 13:00:00', 'checkin', 'NV002', 'CA002', 'VT002',  16.04590, 108.24151),
('AT004', '2024-10-01 17:00:00', 'checkout', 'NV002', 'CA002', 'VT002',  16.04590, 108.24151),
('AT005', '2024-10-01 08:00:00', 'checkin', 'NV003', 'CA001', 'VT001',  16.04590, 108.24151),
('AT006', '2024-10-01 12:00:00', 'checkout', 'NV003', 'CA001', 'VT002',  16.04590, 108.24151),
('AT007', '2024-10-01 13:00:00', 'checkin', 'NV004', 'CA002', 'VT001',  16.04590, 108.24151),
('AT008', '2024-10-01 17:00:00', 'checkout', 'NV004', 'CA002', 'VT002',  16.04590, 108.24151),
('AT009', '2024-10-01 08:00:00', 'checkin', 'NV005', 'CA001', 'VT002',  16.04590, 108.24151),
('AT010', '2024-10-01 12:00:00', 'checkout', 'NV005', 'CA001', 'VT001',  16.04590, 108.24151);

-- Thêm dữ liệu mẫu cho bảng LeaveRequestApproval
INSERT INTO LeaveRequestApproval (LeaveApprovalID, LeaveID, EmployeeID, Status) VALUES
('LAP001', 'DT001', 'NV006', 'Đồng ý'),
('LAP002', 'DT002', 'NV007', 'Chưa phê duyệt'),
('LAP003', 'DT003', 'NV008', 'Đồng ý'),
('LAP004', 'DT004', 'NV009', 'Đồng ý'),
('LAP005', 'DT005', 'NV010', 'Chưa phê duyệt'),
('LAP006', 'DT006', 'NV006', 'Đồng ý'),
('LAP007', 'DT007', 'NV007', 'Chưa phê duyệt'),
('LAP008', 'DT008', 'NV008', 'Đồng ý'),
('LAP009', 'DT009', 'NV009', 'Chưa phê duyệt'),
('LAP010', 'DT010', 'NV010', 'Đồng ý');
