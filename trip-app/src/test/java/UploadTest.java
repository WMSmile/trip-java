import com.cppteam.common.util.ImageUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.awt.*;
import java.io.*;

import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Created by happykuan on 2017/10/30.
 */
public class UploadTest {

    @Test
    public void upload() {

        File file = new File("D:\\图片\\Stones.png");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://journey.xiaokuango.com/app/image/upload");

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(1800000)
                    .setConnectionRequestTimeout(1800000)
                    .setSocketTimeout(1800000).build();

            FileBody bin = new FileBody(file);
            HttpEntity reqEntinty = MultipartEntityBuilder.create()
                    // 相当于<input type="file" name="image"/>
                    .addPart("image", bin)
                    .build();
            httpPost.setConfig(requestConfig);
            long l = System.currentTimeMillis();
            httpPost.setEntity(reqEntinty);

            // 发起请求 并返回请求的响应
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            int len = 0;
            byte[] bytes = new byte[256];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = content.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            baos.close();
            content.close();
            byte[] bytes1 = baos.toByteArray();
            String s = new String(bytes1);
            System.out.println(s);
            long l1 = System.currentTimeMillis();
            System.err.println((l1 - l)/1000);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void thumb() {
        File file = new File("D:\\图片\\Stones.png");
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            baos.flush();

            byte[] byteArray = baos.toByteArray();

            byte[] b = ImageUtils.thumbnailImage(byteArray, 600);

            File f = new File("d:/img.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(b);
            fileOutputStream.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {

            }
        }
    }

    @Test
    public void update() {
        File file = new File("D:\\图片\\Stones.png");
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            baos.flush();

            byte[] byteArray = baos.toByteArray();

            byte[] b = ImageUtils.thumbnailImage(byteArray, 600);

//            ImageUtils.modifyImage(b, "group1/M00/00/01/rBK5IloS7fGAZ3Y6AAkShXpbOjQ968.png");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {

            }
        }
    }

}
