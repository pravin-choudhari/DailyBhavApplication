package com.valuestocks.dailybhav.dailydata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class NseDailyData implements DailyData {
  private static final Logger l = LoggerFactory.getLogger(NseDailyData.class);
  private static final char CSV_SEPARATOR = ',';
  final HttpHeaders headers = new HttpHeaders();
  private final StringBuilder recyclableSb = new StringBuilder();
  private final String nseUrl;
  private final RestTemplate restTemplate;
  @Autowired
  private final ReportDateService reportDateService;

  @Autowired
  public NseDailyData(@Value("${nse.daily.bhav.url}") String nseUrl,
      final ReportDateService reportDateService) {
    this.nseUrl = nseUrl;
    this.reportDateService = reportDateService;
    this.restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
    headers.setAccept(Arrays.asList(MediaType.ALL));
  }

  private static final void deleteDir(final File file) {
    if (file.isDirectory()) {
      for (File subFile : file.listFiles()) {
        if (subFile.isDirectory()) {
          deleteDir(subFile);
        } else {
          deleteFile(subFile);
        }
      }
    }

    deleteFile(file);
  }

  private static final void deleteFile(final File file) {
    try {
      file.delete();
    } catch (final Exception e) {
      l.warn("Error deleting file: {}, cause: {}", file.getAbsolutePath(), e.getCause());
    }
  }

  private String stringifyDate(LocalDate date) {
    final int day = date.getDayOfMonth();
    final int month = date.getMonthValue();
    final String year = String.valueOf(date.getYear());
    final String sDay = day < 10 ? "0" + day : String.valueOf(day);
    final String sMonth = month < 10 ? "0" + month : String.valueOf(month);
    final String reportDate = sDay + sMonth + year.substring(2);

    return reportDate;
  }

  @Override
  public List<String[]> getDailyData(final LocalDate date) {
    final HttpEntity<String> entity = new HttpEntity<String>(headers);
    final String reportDate = stringifyDate(date);
    ResponseEntity<byte[]> response = null;

    try {
      response = restTemplate.exchange(
          nseUrl + reportDate + ".zip",
          HttpMethod.GET, entity, byte[].class);
    } catch (final HttpClientErrorException e) {
      if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        if (date.isBefore(LocalDate.now())) {
          l.info("{} is a holiday, marking it complete and moving to next day", date);
          reportDateService.completeReportUpdate(date);
          return null;
        }
      }

      l.warn("Error", e.getStatusCode());
    }

    File tmpDir = null;

    try {
      tmpDir = Files.createTempDirectory("nse").toFile();
      final String tmpPath = tmpDir.getAbsolutePath();

      Files.write(Paths.get(tmpPath + "/141020.zip"), response.getBody());

      l.info("Downloaded temp directory at: {} ", tmpPath);
      return readStockData(tmpPath + "/141020.zip", tmpPath);

    } catch (IOException e) {
      l.warn("Exception while creating price data file: ", e);
    } finally {
      if (tmpDir != null) {
        deleteDir(tmpDir);
      }
    }

    return null;
  }

  private List<String[]> readStockData(String zipFile, String destDir) throws IOException {
    String priceDataFile = null;

    try {
      ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry zipEntry = zipIn.getNextEntry();

      while (zipEntry != null) {
        final String fileName = zipEntry.getName();

        String filePath = destDir + File.separator + fileName;

        if (!zipEntry.isDirectory() && fileName.startsWith("Pd")) {
          priceDataFile = filePath;

          extractFile(zipIn, filePath);
        }

        zipIn.closeEntry();
        zipEntry = zipIn.getNextEntry();
      }

      zipIn.close();

    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (priceDataFile != null) {
      return Files.lines(Paths.get(priceDataFile))
          .filter(line -> !line.startsWith("MKT"))
          .map(line -> {
            final String[] record = new String[20];
            int i = 0;

            char[] charArray = line.toCharArray();
            for (char c : charArray) {
              if (c != CSV_SEPARATOR) {
                recyclableSb.append(c);
              } else {
                record[i++] = recyclableSb.toString().strip();
                recyclableSb.setLength(0);
              }
            }

            if (recyclableSb.length() > 0) {
              record[i++] = recyclableSb.toString().strip();
            }

            return record;
          }).collect(Collectors.toList());
    }

    return null;
  }

  private void extractFile(ZipInputStream zipIn, String filePath) throws FileNotFoundException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[4096];
    int read = 0;
    try {
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      bos.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
