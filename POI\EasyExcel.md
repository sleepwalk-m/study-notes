# POI和EasyExcel
### POT容易出现内存溢出，譬如说百万级的数据，POI是先读取到内存中，再写入磁盘，那么势必会占用大量内存，而EasyExcel有一些优化，他在解析Excel时并不是一次性全部加载在内存中，而是从磁盘上一行行读取数据的，逐个解析。
#### 注意：excel有03 和 07 版 两者解析有区别
 - *                      对象不同      数据量           文件后缀
 - *                  03: HSSF            最多65536行        xls
 - *                  07: XSSF SXSSF      原则上无限制        xlsx
- 03版创建表
~~~JAVA
@Test
    public void testWrite03() throws Exception {
        // 1. 创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2. 创建工作表
        Sheet sheet = workbook.createSheet("Mask学习POI写入Excel-03版本");
        // 3. 创建行
        Row row = sheet.createRow(0);
        // 4. 创建列
        Cell cell11 = row.createCell(0);
        // 5. 填充数据
        cell11.setCellValue("狂神说观众人数");
        Cell cell12 = row.createCell(1);
        cell12.setCellValue(666);

        Row row2 = sheet.createRow(1);
        Cell cell21 = row2.createCell(0);
        Cell cell22 = row2.createCell(1);
        cell21.setCellValue("统计日期");
        String time = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        cell22.setCellValue(time);

        // 用流写出 03版本 格式是xls
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "Mask学习POI写入Excel-03版本.xls");
        workbook.write(fileOutputStream);

        //关流
        fileOutputStream.close();

    }

~~~
