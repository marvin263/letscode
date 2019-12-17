/**
 * Copyright (C) 2006-2019 Tuniu All rights reserved
 */
package com.tuniu.ocr;

import java.io.File;
import java.net.URL;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sourceforge.tess4j.util.LoadLibs;

/**
 * TODO: description
 * Date: 2019-08-28
 *
 * @author zouchuanhua
 */
@RestController
@RequestMapping("ocr")
public class OcrController {

    @RequestMapping("hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("bbbb")
    public String bbbb() throws Exception {
        BytePointer outText = null;
        tesseract.TessBaseAPI api = null;
        lept.PIX image = null;
        try {
            api = new tesseract.TessBaseAPI();
//            String dataPath = OcrController.class.getClassLoader().getResource("tessdata").getPath();
            File tessdata = LoadLibs.extractTessResources("tessdata");
            String dataPath = tessdata.getAbsolutePath();
            System.out.println("tessdata path--" + dataPath);
            int i = api.Init(dataPath, "eng");
            System.out.println("api init result---" + i);
            URL url = new URL("https://s.tuniu.net/qn/image/d5b4bd5781a7344fd43b6d27fb074979/8ff06866-f277-4c0f-b9ac-f6c0071bc6be.jpg");
            File file = Loader.cacheResource(url);
            image = lept.pixRead(file.getAbsolutePath());
            long start = System.currentTimeMillis();
            api.SetImage(image);
            api.SetRectangle(580, 1525, 225, 50);
            outText = api.GetUTF8Text();
            return outText.getString() + "--------" + (System.currentTimeMillis() - start) + "ms";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (api != null) {
                api.End();
            }
            if (outText != null) {
                outText.deallocate();
            }
            lept.pixDestroy(image);
        }
        return "111";
    }

}
