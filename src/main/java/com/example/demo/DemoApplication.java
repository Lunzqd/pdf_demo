package com.example.demo;

import com.example.utils.CommonUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {

    }

    // 临时文件夹获取java system变量中的临时路径，在web项目中是容器的temp文件夹,如果直接运行是系统临时文件夹.
    private static final String FILE_PATH_TEMPLATE = System.getProperty("java.io.tmpdir") + "/tempdf/%s";

    public String testDemo() throws IOException {

        Map<String, Object> data = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 1.模板准备
        // 在线文档需先下载
        String templateUrl = "./FinTemplate/测试.pdf";

        // 2.数据准备
        data.put("name", "小明");
        data.put("man", "ON");
        data.put("phoneNumber", "15066667777");
        data.put("contractTitle", "标题加粗");

        // 3.确定pdf文件生成位置
        String fileName = templateUrl.substring(templateUrl.lastIndexOf("/") + 1);
        // 临时文件名（模板名+ 年月日）
        String pdfTempName = fileName.substring(0, fileName.indexOf(".")) + "_" + sdf.format(new Date()) + ".pdf";
        // 生成临时文件位置
        String newfilePath = "./PdfFile/" + pdfTempName;
        File file = new File(newfilePath);
        //如果不存在临时文件夹，则创建文件夹
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        // 4.填充pdf文件
        PdfReader pdfReader = new PdfReader(templateUrl);
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(file));
        // 4.1 创建pdf文件
        PdfDocument pdf = new PdfDocument(pdfReader, pdfWriter);
        try {
            //4.2、创建中文字体
            String fontUrl = "./Fonts/msyh.ttc";// 微软雅黑字体
            FontProgram fontProgram = FontProgramFactory.createFont(fontUrl + ",1");
            PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, true);
            //4.3、获取pdf模板中的域值信息
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fieldMap = form.getFormFields();
            Iterator<String> paramIterator = data.keySet().iterator();
            while (paramIterator.hasNext()) {
                String key = paramIterator.next();
                String value = "";
                if (!CommonUtil.isObjectNull(data.get(key))) {
                    value = data.get(key).toString();
                }

                PdfFormField formField = fieldMap.get(key);
                if (formField == null) {
                    continue;
                } else {
                    if ("ON".equals(value)) {
                        formField.setCheckType(1).setValue("");// 勾选
                    }
                }
                if ("contractTitle".equals(key)) {
                    // 合同标题字体：微软雅黑加粗
                    FontProgram boldProgram = FontProgramFactory.createFont("./Fonts/msyhbd.ttc,1");
                    formField.setValue(value).setFont(PdfFontFactory.createFont(boldProgram, PdfEncodings.IDENTITY_H, true));
                } else {
                    formField.setValue(value).setFont(font);
                }
            }
            //4.4、设置文本不可编辑
            form.flattenFields();
            pdf.close();
            pdfWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("生成PDF文件出现异常，原因：%s %n %s", e.getMessage(), e.getStackTrace()));
            pdf.close();
            file.delete();
        }
        return newfilePath;
    }

    public void addPicture() throws IOException {
        String templateUrl = this.getClass().getResource("/FinTemplate/测试2.pdf").getPath();
        String fontUrl = this.getClass().getResource("/Fonts/msyh.ttc").getPath();// 微软雅黑字体
        FontProgram fontProgram = FontProgramFactory.createFont(fontUrl + ",1");
        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H, true);
/*        PdfWriter pdfWriter = new PdfWriter(templateUrl);
        PdfDocument pdf = new PdfDocument(pdfWriter);
        Document document = new Document(pdf);
//        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        //添加图片
        String foxPath = this.getClass().getResource("/img/fox.jpg").getPath();
        String dogPath = this.getClass().getResource("/img/dog.jpg").getPath();
        Image fox = new Image(ImageDataFactory.create(foxPath));
        Image dog = new Image(ImageDataFactory.create(dogPath));
        Paragraph p = new Paragraph("The quick brown ")
                .add(fox)
                .add(" jumps over the lazy ")
                .add(dog);
        p.setFont(font);
        document.add(p);
        document.close();
        pdf.close();
        pdfWriter.flush();
        pdfWriter.close();*/

        PdfWriter writer = new PdfWriter(templateUrl);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        //添加文本
        document.add(new Paragraph("Hello World!").setFont(font));
        document.close();
    }

}
