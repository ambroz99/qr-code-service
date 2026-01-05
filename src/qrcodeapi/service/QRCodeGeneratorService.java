package qrcodeapi.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

@Service
public class QRCodeGeneratorService {
    private static final Map<String, ErrorCorrectionLevel> ERROR_CORRECTION_LEVEL_MAP =
            Map.of(
                    "L", ErrorCorrectionLevel.L,
                    "M", ErrorCorrectionLevel.M,
                    "Q", ErrorCorrectionLevel.Q,
                    "H", ErrorCorrectionLevel.H
            );

    public BufferedImage createQRCode(String contents, int size, String correction) {
        Map<EncodeHintType, ?> hints =
                Map.of(EncodeHintType.ERROR_CORRECTION, ERROR_CORRECTION_LEVEL_MAP.get(correction));
        QRCodeWriter writer = new QRCodeWriter();
        BufferedImage bufferedImage;
        try {
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        return bufferedImage;
    }
}
