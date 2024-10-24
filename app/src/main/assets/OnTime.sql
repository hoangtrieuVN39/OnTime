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
    AccountID VARCHAR(10) PRIMARY KEY,
    Passwordd VARCHAR(255),
    EmployeeID VARCHAR(10),
    EmployeeEmail VARCHAR(100),
     FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
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
    CreatedTime DATETIME,
    AttendanceType VARCHAR(50),
    EmployeeID VARCHAR(10),
    ShiftID  VARCHAR(10),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (ShiftID) REFERENCES WorkShift(ShiftID)
);

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

INSERT INTO Account (AccountID, Passwordd, EmployeeID, EmployeeEmail) VALUES
    ('TK001', 'password1', 'NV001', 'nva@example.com'),
    ('TK002', 'password2', 'NV002', 'ltb@example.com'),
    ('TK003', 'password3', 'NV003', 'tvc@example.com'),
    ('TK004', 'password4', 'NV004', 'ptd@example.com'),
    ('TK005', 'password5', 'NV005', 'hve@example.com'),
    ('TK006', 'password6', 'NV006', 'ntf@example.com'),
    ('TK007', 'password7', 'NV007', 'lvg@example.com'),
    ('TK008', 'password8', 'NV008', 'tth@example.com'),
    ('TK009', 'password9', 'NV009', 'pvi@example.com'),
    ('TK010', 'password10', 'NV010', 'htk@example.com');

INSERT INTO LeaveType (LeaveTypeID, LeaveTypeName) VALUES
('LDT001', 'Đi muộn/Về sớm (trong vòng 1 giờ)'),
('LDT002', 'Nghỉ không lương'),
('LDT003', 'Xin nghỉ phép (trước 24 giờ)'),
('LDT004', 'Nghỉ cưới/tang lễ'),
('LDT005', 'Công tác'),
('LDT006', 'Làm việc từ xa'),
('LDT007', 'Giải trình'),
('LDT008', 'Nghỉ phép có lương'),
('LDT009', 'Nghỉ ốm'),
('LDT010', 'Nghỉ thai sản');

INSERT INTO WorkShift (ShiftID, ShiftName, StartTime, EndTime ) VALUES
('CA001', 'Morning shift', '08:00:00', '12:00:00'),
('CA002', 'Afternoon shift', '13:00:00', '17:00:00'),
('CA003', 'Evening Shift', '18:00:00', '22:00:00');

INSERT INTO LeaveRequest (RequestID, CreatedTime, Statuss, LeaveTypeID, EmployeeID, LeaveRequestApprovalID) VALUES
('DT001', '2024-01-01 08:00:00', 'Accept', 'LDT003', 'NV003'),
('DT002', '2024-01-05 08:00:00', 'Accept', 'LDT005', 'NV002'),
('DT003', '2024-01-10 08:00:00', 'Accept', 'LDT001', 'NV001'),
('DT004', '2024-01-15 08:00:00', 'Pending approval', 'LDT006', 'NV005'),
('DT005', '2024-01-20 08:00:00', 'Refused', 'LDT004', 'NV003'),
('DT006', '2024-01-25 08:00:00', 'Accept', 'LDT002', 'NV002'),
('DT007', '2024-02-01 08:00:00', 'Pending approval', 'LDT003', 'NV001'),
('DT008', '2024-02-05 08:00:00', 'Refused', 'LDT005', 'NV006'),
('DT009', '2024-02-10 08:00:00', 'Accept', 'LDT001', 'NV002'),
('DT010', '2024-02-15 08:00:00', 'Pending approval', 'LDT007', 'NV007');

INSERT INTO LeaveRequestApproval (ApprovalID, CreatedTime, Statuss, EmployeeID) VALUES
('LA001', '2024-01-01 08:00:00', 'Accept', 'NV003'),
('LA002', '2024-01-05 08:00:00', 'Accept', 'NV002'),
('LA003', '2024-01-10 08:00:00', 'Accept', 'NV001'),
('LA004', '2024-01-15 08:00:00', 'Pending approval', 'NV005'),
('LA005', '2024-01-20 08:00:00', 'Refused', 'NV003'),
('LA006', '2024-01-25 08:00:00', 'Accept', 'NV002'),
('LA007', '2024-02-01 08:00:00', 'Pending approval', 'NV001'),
('LA008', '2024-02-05 08:00:00', 'Refused', 'NV006'),
('LA009', '2024-02-10 08:00:00', 'Accept', 'NV002'),
('LA010', '2024-02-15 08:00:00', 'Pending approval', 'NV007');

-- Insert dữ liệu vào bảng ChamCong
INSERT INTO Attendance  (CreatedTime, AttendanceType, EmployeeID, ShiftID) VALUES
('2024-01-01 08:00:00', 'Check in', 'NV001', 'CA001'),
('2024-01-05 13:15:00', 'Check in', 'NV002', 'CA002'),
('2024-01-10 07:30:00', 'Check in', 'NV003', 'CA001'),
('2024-01-10 12:30:00', 'Check out', 'NV003', 'CA001'),
('2024-01-15 12:05:00', 'Check out', 'NV004', 'CA001'),
('2024-01-20 17:05:00', 'Check out', 'NV005', 'CA002'),
('2024-01-25 18:05:00', 'Check in', 'NV006', 'CA003'),
('2024-02-01 08:20:00', 'Check in', 'NV007', 'CA001'),
('2024-02-05 17:10:00', 'Check out', 'NV008', 'CA002'),
('2024-02-10 18:35:00', 'Check in', 'NV009', 'CA003'),
('2024-02-15 08:50:00', 'Check in', 'NV010', 'CA001');