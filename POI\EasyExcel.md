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
- 07版大量数据写入
- 注意： 有XSSF 和 SXSSF两种对象可以用
SXSSF的用时比XSSF短，原因是在于SXSSF使用的是临时文件的形式，默认有100条记录保存在内存中，如果超过这个数量，则最前面的数据写入临时文件，也可以自定义这个数据量：
new SXSSFWorkbook(数量)
但并不是绝对不会造成内存溢出，取决于进行的操作，比如合并区域、注释等只能存储在内存中，如果用的多的话，也是有可能内存溢出的

  - XSSF的方式
  ~~~java
  /**
     * 07版大数据量写入
     *  xssf对象写入 耗时久
     *
     */
    @Test
    public void testWrite07BigData() throws Exception{
        long start = System.currentTimeMillis();

        // 1. 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 2. 创建工作表
        Sheet sheet = workbook.createSheet("07版大数据量写入");
        // 3. 循环写入数据
        for (int rowNum = 0; rowNum < 100000; rowNum++) {
            Row row = sheet.createRow(rowNum);
            for (int cellNum = 0; cellNum < 10; cellNum++) {
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(cellNum);
            }
        }

        // 4. 写出数据
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "07版大数据量写入.xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        // 4. 计算时间
        long end = System.currentTimeMillis();
        Double time = (double)(end - start)/1000;
        System.out.println("数据写出成功！用时" + time + "秒");
    }
  ~~~
  
  - SXSSF的方式
  ~~~java
  /**
     * 07版大数据量写入
     *  SXSSF对象写入 耗时短
     *     使用的是临时文件的形式
     *     默认有100条数据加载到内存，超过这个数据就生成临时文件，将100条存入临时文件再进行操作，后续的数据就循环往复
     *     可以用 new SXSSFWorkbook(数量)自定义初始数据量
     *
     */
    @Test
    public void testWrite07BigDataSXSSF() throws Exception{
        long start = System.currentTimeMillis();

        // 1. 创建工作簿 这里用的是SXSSFWorkbook 会产生临时文件，在写完之后注意删除临时文件
        Workbook workbook = new SXSSFWorkbook();
        // 2. 创建工作表
        Sheet sheet = workbook.createSheet("07版大数据量写入");
        // 3. 循环写入数据
        for (int rowNum = 0; rowNum < 100000; rowNum++) {
            Row row = sheet.createRow(rowNum);
            for (int cellNum = 0; cellNum < 10; cellNum++) {
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(cellNum);
            }
        }

        // 4. 写出数据
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "07版大数据量写入-SXSSF.xlsx");
        workbook.write(fileOutputStream);
        // 删除临时文件
        ((SXSSFWorkbook) workbook).dispose();
        fileOutputStream.close();
        // 4. 计算时间
        long end = System.currentTimeMillis();
        Double time = (double)(end - start)/1000;
        System.out.println("数据写出成功！用时" + time + "秒");
    }
    ~~~
