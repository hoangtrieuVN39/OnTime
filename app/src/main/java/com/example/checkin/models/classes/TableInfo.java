package com.example.checkin.models.classes;

public class TableInfo {
    public String tableName;
    public String[] columnNames;

    public TableInfo(String tableName, String[] columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames;
    }
    public String getTableName() {
        return tableName;
    }
    public String[] getColumnNames() {
        return columnNames;
    }
    public String getColumnName(int index) {
        return columnNames[index];
    }
    public int getColumnCount() {
        return columnNames.length;
    }
}
