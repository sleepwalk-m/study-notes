# POI和EasyExcel
### POI容易出现内存溢出，譬如说百万级的数据，POI是先读取到内存中，再写入磁盘，那么势必会占用大量内存，而EasyExcel有一些优化，他在解析Excel时并不是一次性全部加载在内存中，而是从磁盘上一行行读取数据的，逐个解析。
#### 注意：excel有03 和 07 版 两者解析有区别
 - *                      对象不同                           数据量           文件后缀
 - *                  03: HSSF                              最多65536行        xls
 - *                  07: XSSF、SXSSF（处理大数据量的对象）   原则上无限制        xlsx

# POI的使用



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
- 07版创建表
~~~java
@Test
    public void testWrite07() throws Exception{
        // 1. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 2. 创建工作表
        Sheet sheet = workbook.createSheet("Mask学习POI写入Excel-07版本");
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

        // 用流写出 03版本 格式是xlsx
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "Mask学习POI写入Excel-07版本.xlsx");
        workbook.write(fileOutputStream);

        //关流
        fileOutputStream.close();

    }
~~~
- 03版写入大量数据
  注意： 03最多只能写入65536行数据，超过了就会抛出异常：
  ~~~java
  java.lang.IllegalArgumentException: Invalid row number (65536) outside allowable range (0..65535)
  ~~~
  ~~~java
  /**
     *  03版的大数据量的写入
     *
     */
    @Test
    public void testWrite03BigData() throws Exception{
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建工作表
        Sheet sheet = workbook.createSheet("03版大数据量写入");
        // 3.写入数据
        for (int rowNum = 0; rowNum < 65536; rowNum++) {
            // 循环65536次写入行，每一行写10列
            Row row = sheet.createRow(rowNum);
            for (int cellNum = 0; cellNum < 10; cellNum++) {
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(cellNum);
            }
        }

        // 4.写出文件 03 xls
        FileOutputStream outputStream = new FileOutputStream(PATH + "03版大数据量写入.xls");
        workbook.write(outputStream);
        outputStream.close();

        System.out.println("数据写出成功！");
    }
 ~~~
