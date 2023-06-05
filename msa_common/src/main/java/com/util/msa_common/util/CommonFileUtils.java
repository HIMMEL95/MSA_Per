package com.per.msa_common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("commonFileUtils")
public class CommonFileUtils {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private S3FileUtils s3FileUtils;

    @Value("#{config['path.file']}")
    private String filePath;
    @Value("#{config['path.sampleFile']}")
    private String sampleFilePath;

    @Value("#{config['path.resultFile']}")
    private String resultFilePath;

    @Value("#{config['path.companyBizResultFile']}")
    private String companyBizResultFile;

    @Value("#{config['path.clientBizResultFile']}")
    private String clientBizResultFile;

    @Value("#{config['path.projectBizResultFile']}")
    private String projectBizResultFile;

    @Value("#{config['path.partnerCooperationFile']}")
    private String partnerCooperationFile;

    @Value("#{config['path.uploadFile']}")
    private String uploadFilePath;

    @Value("#{config['path.image']}")
    private String ImageFilePath;

    @Value("#{config['path.calUserInfo']}")
    private String calUserInfo;

    public String getDirectoryName(String tableName) {
        if (tableName.equals("BIZ_FILE_STORE")) {
            tableName = "BIZ_FILE_STORE/";
        } else if (tableName.equals("CLIENT_COMPANY_INFO_FILE_STORE")) {
            tableName = "CLIENT_COMPANY_INFO_FILE_STORE/";
        } else if (tableName.equals("CLIENT_EVENT_FILE_STORE")) {
            tableName = "CLIENT_EVENT_FILE_STORE/";
        } else if (tableName.equals("OPPORTUNITY_FILE_STORE")) {
            tableName = "OPPORTUNITY_FILE_STORE/";
        } else if (tableName.equals("CLIENT_ISSUE_FILE_STORE")) {
            tableName = "CLIENT_ISSUE_FILE_STORE/";
        } else if (tableName.equals("CLIENT_PUNCHING_FILE_STORE")) {
            tableName = "CLIENT_PUNCHING_FILE_STORE/";
        } else if (tableName.equals("PARTNER_SALES_LINAKGE_FILE_STORE")) {
            tableName = "PARTNER_SALES_LINAKGE_FILE_STORE/";
        } else if (tableName.equals("RIVAL_COMPANY_FILE_STORE")) {
            tableName = "RIVAL_COMPANY_FILE_STORE/";
        } else if (tableName.equals("PROPOSAL_FILE_STORE")) {
            tableName = "PROPOSAL_FILE_STORE/";
        } else if (tableName.equals("BIZ_PROJECT_PLAN_FILE_STORE")) {
            tableName = "BIZ_PROJECT_PLAN_FILE_STORE/";
        }
        return tableName;
    }

    public List<Map<String, Object>> insertFile(Map<String, Object> map, List<Map<String, Object>> fileList,
            String fileTableName, String fileColumnName) throws Exception {
        String directoryName = getDirectoryName(fileTableName);

        String originalFileName = null;
        String originalFileExtension = null;

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> listMap = null;

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<HashMap<String, String>> fileDatalist = new ArrayList<HashMap<String, String>>();
        String jsonData = String.valueOf(map.get("fileData"));
        fileDatalist = mapper.readValue(jsonData, new TypeReference<ArrayList<HashMap<String, String>>>() {
        });

        int filePK = Integer.parseInt(map.get("filePK").toString());
        String creatorId = (String) map.get("hiddenModalCreatorId");

        /*
         * 시스템 날짜 월 폴더 생성 ex) 201605
         */
        String thismonth = CommonUtils.currentDate("yyyyMM") + "/";
        String filePathThisMonth = filePath + thismonth + directoryName;
        String now = String.valueOf(System.currentTimeMillis()); // 현재시간

        int fileIndex = 0;
        for (Map<String, Object> fileMap : fileList) {
            if (fileMap.get("file") != null
                    && !"false".equals(String.valueOf(fileDatalist.get(fileIndex).get("useYN")).toString())) {
                originalFileName = (String) fileMap.get("originalFileName");
                originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

                byte[] fileByte = (byte[]) fileMap.get("file");

                int idx = originalFileName.lastIndexOf("."); // 확장자명 *번째
                originalFileName = originalFileName.substring(0, idx); // originalFileName변수를
                                                                       // 확장자명을
                                                                       // 뺀 파일
                                                                       // 이름으로
                                                                       // 바꿈.
                originalFileName = originalFileName + "_" + now; // 파일명_현재시간

                String rsUrl = s3FileUtils.upload(
                        fileByte,
                        filePathThisMonth + originalFileName + originalFileExtension,
                        (String) fileMap.get("fileContentType"));
                log.info("S3 upload ::: " + rsUrl);

                listMap = new HashMap<>();
                listMap.put("filePK", filePK);
                listMap.put("originalFileName", originalFileName + originalFileExtension);
                listMap.put("originalFileExtension", originalFileExtension);
                listMap.put("filePath", thismonth + directoryName);
                listMap.put("creatorId", creatorId);
                listMap.put("fileTableName", fileTableName);
                listMap.put("fileColumnName", fileColumnName);

                list.add(listMap);
            }
            fileIndex++;
        }

        return list;
    }

    public void deleteFile(Map<String, Object> map) {
        log.info("deleteFile..");

        String fileName = filePath + map.get("FILE_PATH") + map.get("FILE_NAME");
        s3FileUtils.delete(fileName);
    }

    public void deleteFileAll(List<Map<String, Object>> list) {
        log.info("deleteFileAll..");

        for (int i = 0; i < list.size(); i++) {
            String fileName = filePath + list.get(i).get("FILE_PATH") + list.get(i).get("FILE_NAME");
            s3FileUtils.delete(fileName);
        }
    }

    public Map<String, Object> downloadFile(Map<String, Object> map) throws Exception {
        log.info("downloadFile..");
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String fileName = "" + map.get("FILE_PATH") + map.get("FILE_NAME");
            if (map.get("FILE_PATH").toString().contains("organizationChart/")
                    || map.get("FILE_PATH").toString().contains("photo/")) {
                fileName = ImageFilePath + fileName;
            } else {
                fileName = filePath + fileName;
            }
            byte[] fileData = s3FileUtils.download(fileName);
            String name = new String(((String) map.get("FILE_NAME")).getBytes("UTF-8"), "ISO-8859-1");
            resultMap.put("errorFlag", "0");
            resultMap.put("fileData", fileData);
            resultMap.put("fileName", name);
        } catch (Exception e) {
            resultMap.put("errorFlag", "-1");
        }

        return resultMap;

    }

    public Map<String, Object> sampleDownloadFile(Map<String, Object> map) throws Exception {
        log.info("sampleDownloadFile..");
        log.info(map.toString());
        Map<String, Object> resultMap = new HashMap<>();
        String filePath = "";
        int delCnt = 0;

        try {
            if (map.get("menuFlag").equals("companyBizFile")) { // 회사/부문별
                filePath = companyBizResultFile;
            } else if (map.get("menuFlag").equals("clientBizFile")) { // 고객별전략
                filePath = clientBizResultFile;
            } else if (map.get("menuFlag").equals("projectBizFile")) { // 전략프로젝트
                filePath = projectBizResultFile;
            } else if (map.get("menuFlag").equals("excelFile")) { // 관리자 파일관리
                delCnt = 1;
                filePath = resultFilePath;
            } else if (map.get("menuFlag").equals("partnerCooperationSalesFile")) { // 파트너협업관리_파트너현황
                filePath = partnerCooperationFile;
            } else {
                delCnt = 1;
                filePath = resultFilePath;
            }

            String fileName = filePath + map.get("sampleFileName");

            log.info("fileName:: " + fileName);
            byte[] fileData = s3FileUtils.download(fileName);
            String name = new String(((String) map.get("sampleFileName")).getBytes("UTF-8"), "ISO-8859-1");

            resultMap.put("errorFlag", "0");
            resultMap.put("fileData", fileData);
            resultMap.put("fileName", name);

            if (delCnt == 1) { // 엑셀 업로드 결과 다운로드만 파일 삭제
                deleteExel(fileName);
            }
        } catch (Exception e) {
            resultMap.put("errorFlag", "-1");
        }

        return resultMap;
    }

    private void deleteExel(String filePath) {

        s3FileUtils.delete(filePath);
    }

    public Map<String, Object> sampleExcelDownloadFile(Map<String, Object> map) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String fileName = sampleFilePath + map.get("sampleFileName");
        log.info("fileName:: " + fileName);
        byte[] fileData = s3FileUtils.download(fileName);
        String name = new String(((String) map.get("sampleFileName")).getBytes("UTF-8"), "ISO-8859-1");

        try {
            resultMap.put("errorFlag", "0");
            resultMap.put("fileData", fileData);
            resultMap.put("fileName", name);
        } catch (Exception e) {
            resultMap.put("errorFlag", "-1");
        }

        // 양식 다운 완료후 파일 삭제
        deleteExel(fileName);

        return resultMap;
    }

    public Map<String, Object> insertPhoto(Map<String, Object> map, Map<String, Object> photo, String fileTableName,
            String fileColumnName) throws Exception {

        String companyId = "";
        Map<String, Object> photoMap = (Map<String, Object>) photo.get("fileModalUploadPhoto");

        if (fileTableName.equals("CLIENT_INDIVIDUAL_PHOTO_STORE")) {
            companyId = (String) map.get("imageType") + "/client/" + (String) map.get("textCommonSearchCompanyId")
                    + "/";
            photoMap = (Map<String, Object>) photo.get((String) map.get("fileModalUploadType"));
        } else if (fileTableName.equals("PARTNER_INDIVIDUAL_PHOTO_STORE")) {
            companyId = (String) map.get("imageType") + "/partner/" + (String) map.get("textCommonSearchCompanyId")
                    + "/";
            photoMap = (Map<String, Object>) photo.get((String) map.get("fileModalUploadType"));
        } else if (fileTableName.equals("CLIENT_COMPANY_INFO_FILE_STORE")) {
            companyId = "organizationChart/client/";
        } else if (fileTableName.equals("PARTNER_COMPANY_INFO_FILE_STORE")) {
            companyId = "organizationChart/partner/";
        }

        String originalFileName = null;
        String originalFileExtension = null;

        Map<String, Object> listMap = null;

        int filePK = Integer.parseInt(map.get("filePK").toString());
        String creatorId = (String) map.get("hiddenModalCreatorId");

        String filePathThisCompanyId = ImageFilePath + companyId;

        if (photoMap != null) {
            originalFileName = (String) photoMap.get("originalFileName");
            originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 파일명 = filePK_현재시간
            originalFileName = String.valueOf(filePK) + "_" + System.currentTimeMillis();

            byte[] fileByte = (byte[]) photoMap.get("file");

            String rsUrl = s3FileUtils.upload(
                    fileByte,
                    filePathThisCompanyId + originalFileName + originalFileExtension,
                    (String) photoMap.get("fileContentType"));
            log.info("S3 upload ::: " + rsUrl);

            listMap = new HashMap<>();
            listMap.put("filePK", filePK);
            listMap.put("originalFileName", originalFileName + originalFileExtension);
            listMap.put("originalFileExtension", originalFileExtension);
            listMap.put("filePath", companyId);
            listMap.put("creatorId", creatorId);
            listMap.put("fileTableName", fileTableName);
            listMap.put("fileColumnName", fileColumnName);

        }
        return listMap;
    }

    public void deletePhoto(Map<String, Object> map) {
        String fileName = ImageFilePath + (String) map.get("FILE_PATH") + map.get("FILE_NAME");
        s3FileUtils.delete(fileName);
    }

    public Map<String, Object> movePhoto(Map<String, Object> oldPhoto, Map<String, Object> map,
            Map<String, Object> newPhoto, String fileTableName, String fileColumnName) throws Exception {
        String companyId = "";
        // Map<String, Object> photoMap = (Map<String,
        // Object>)newPhoto.get("fileModalUploadPhoto");

        if (fileTableName.equals("CLIENT_INDIVIDUAL_PHOTO_STORE")) {
            companyId = (String) map.get("imageType") + "/client/" + (String) map.get("textCommonSearchCompanyId")
                    + "/";
            // photoMap = (Map<String, Object>)newPhoto.get((String)
            // map.get("fileModalUploadType"));
        } else if (fileTableName.equals("PARTNER_INDIVIDUAL_PHOTO_STORE")) {
            companyId = (String) map.get("imageType") + "/partner/" + (String) map.get("textCommonSearchCompanyId")
                    + "/";
            // photoMap = (Map<String, Object>)newPhoto.get((String)
            // map.get("fileModalUploadType"));
        } else if (fileTableName.equals("CLIENT_COMPANY_INFO_FILE_STORE")) {
            companyId = "organizationChart/client/";
        } else if (fileTableName.equals("PARTNER_COMPANY_INFO_FILE_STORE")) {
            companyId = "organizationChart/partner/";
        }

        // 기존 경로
        log.info(oldPhoto.toString());
        String photoFilePath = (String) oldPhoto.get("FILE_PATH");
        String photoFileName = (String) oldPhoto.get("FILE_NAME");

        String oldFileName = ImageFilePath + photoFilePath + photoFileName;
        String newFileName = null;

        String originalFileName = null;
        String originalFileExtension = null;

        Map<String, Object> listMap = null;

        int filePK = Integer.parseInt(map.get("filePK").toString());
        String creatorId = (String) map.get("hiddenModalCreatorId");
        String filePathThisCompanyId = ImageFilePath + companyId;

        originalFileName = map.get("filePK").toString() + "_" + System.currentTimeMillis();
        originalFileExtension = (String) oldPhoto.get("FILE_TYPE");

        newFileName = filePathThisCompanyId + originalFileName + originalFileExtension;

        s3FileUtils.rename(oldFileName, newFileName);

        listMap = new HashMap<String, Object>();
        listMap.put("fileId", oldPhoto.get("FILE_ID"));
        listMap.put("filePK", filePK);
        listMap.put("originalFileName", originalFileName + originalFileExtension);
        listMap.put("originalFileExtension", originalFileExtension);
        listMap.put("filePath", companyId);
        listMap.put("creatorId", creatorId);
        listMap.put("fileTableName", fileTableName);
        listMap.put("fileColumnName", fileColumnName);

        return listMap;
    }

    ///// ICS파일 다운로드 경로
    public void downICS(Map<String, Object> map) throws Exception {

        log.debug("downICS Call.");

        URL url;
        byte[] buf;

        url = new URL(map.get("downURL").toString());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        InputStream is = url.openStream();
        buf = new byte[1024];

        double len = 0;

        while ((len = is.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, (int) len);
        }

        String creatorId = (String) map.get("creatorId");
        String filePathThisCreatorId = calUserInfo + "google/in/"; // 서버
        String fileName = filePathThisCreatorId + "." + creatorId + ".ics";

        s3FileUtils.upload(
                bos.toByteArray(),
                fileName,
                "text/*");

        bos.close();
        is.close();
        log.debug("다운로드 완료.");
    }

    /**
     * google 캘린더 삭제시에 Google ICS 파일 삭제.
     * 
     * @param map
     * @throws Exception
     */
    public void deleteFile(Map<String, Object> map, String calUserInfoPath) throws Exception {
        log.debug("deleteFile Call.");

        String creatorId = (String) map.get("hiddenModalCreatorId");
        String fileDir = "";

        if (map.get("calendarName").equals("아웃룩 캘린더")) { // 아웃룩 로그인 정보 파일 삭제

            fileDir = calUserInfoPath + "outlook/" + "." + creatorId + ".txt";

        } else if (map.get("calendarType").equals("3")) { // 구글 ICS 파일 삭제

            fileDir = calUserInfoPath + "google/in/" + "." + creatorId + ".ics";

        } else if (map.get("calendarType").equals("2")) { // Office 365 로그인 정보 파일 삭제.

            fileDir = calUserInfoPath + "office365/" + "." + creatorId + ".txt";
        }
        if (!"".equals(fileDir)) {
            s3FileUtils.delete(fileDir);
        }
    }

    /**
     * 구글 ICS 파일 읽어서 캘린더 정보 가져오기.
     * 
     * @param map
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unused")
    public List<Map<String, Object>> getGoogleIcsEventList(Map<String, Object> map, String calUserInfoPath)
            throws Exception {

        log.debug("getGoogleIcsEventList Call.");

        String creatorId = (String) map.get("hiddenModalCreatorId");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> recurList = new ArrayList<Map<String, Object>>();
        String fileDir = calUserInfoPath + "google/in/" + "." + creatorId + ".ics";

        byte[] fileByte = s3FileUtils.download(fileDir);

        if (fileByte != null && fileByte.length > 0) {

            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByte)));
            Map<String, Object> icsMap = new HashMap<String, Object>();

            String line = null;
            String str = "";
            String location = "";
            String description = "";
            String summary = "";
            String dtStart = "";
            String dtEnd = "";
            String rrule = ""; // 반복일정
            String exDate = ""; // 제외일정
            String uid = ""; // uid 유일값
            String recur_id = ""; // 반복연동시

            try {
                while ((line = in.readLine()) != null) {
                    str = line;

                    if (str.matches(".*UID.*")) {
                        uid = str;
                        uid = uid.substring(uid.indexOf(":") + 1);
                    }

                    if (str.matches(".*RECURRENCE-ID.*")) {
                        Map<String, Object> recurMap = new HashMap<String, Object>();
                        recur_id = str;
                        recur_id = recur_id.substring(recur_id.indexOf(":") + 1);

                        SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd");

                        Date original_date = original_format.parse(recur_id);
                        recur_id = new_format.format(original_date);

                        recurMap.put("uid", uid);
                        recurMap.put("recur_id", recur_id);
                        recurList.add(recurMap);
                    }

                    if (str.matches(".*DTSTART.*")) {
                        dtStart = str;
                        dtStart = dtStart.substring(dtStart.indexOf(":") + 1);

                        // icsMap.put("DTSTART", dtStart);

                        // 심윤영 - ㅅ
                        if (dtStart.length() == 8) { // 종일O
                            icsMap.put("allDay", "1"); // 종일O
                            icsMap.put("START_DAY", dtStart);

                            SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd");
                            SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd");

                            Date original_date = original_format.parse(dtStart);
                            dtStart = new_format.format(original_date);

                            icsMap.put("start", dtStart);
                        } else { // 종일X
                            icsMap.put("allDay", "0"); // 종일X

                            if (dtStart.length() == 15) { // 데이트 포맷 숫자로 끝나는 경우
                                SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd'T'hhmmss");
                                SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                SimpleDateFormat new_format2 = new SimpleDateFormat("yyyyMMdd");
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                                original_format.setTimeZone(timeZone);

                                Date original_date = original_format.parse(dtStart);
                                dtStart = new_format.format(original_date);

                                icsMap.put("START_DAY", new_format2.format(original_date));
                                icsMap.put("start", dtStart);
                            } else if (dtStart.length() == 16) { // 데이트 포맷 Z로 끝나는 경우
                                SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
                                SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                SimpleDateFormat new_format2 = new SimpleDateFormat("yyyyMMdd");
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                                original_format.setTimeZone(timeZone);

                                Date original_date = original_format.parse(dtStart);
                                dtStart = new_format.format(original_date);

                                icsMap.put("START_DAY", new_format2.format(original_date));
                                icsMap.put("start", dtStart);
                            }
                        }
                        // 심윤영 - ㄲ

                        continue;
                    }

                    if (str.matches(".*DTEND.*")) {
                        dtEnd = str;
                        dtEnd = dtEnd.substring(dtEnd.indexOf(":") + 1);

                        // icsMap.put("DTEND", dtEnd);

                        // 심윤영 - ㅅ
                        if (dtEnd.length() == 8) { // 종일O
                            icsMap.put("allDay", "1"); // 종일O
                            icsMap.put("END_DAY", dtEnd);

                            SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd");
                            SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd");

                            Date original_date = original_format.parse(dtEnd);
                            dtEnd = new_format.format(original_date);

                            icsMap.put("end", dtEnd);
                        } else { // 종일X
                            icsMap.put("allDay", "0"); // 종일X

                            if (dtEnd.length() == 15) { // 데이트 포맷 숫자로 끝나는 경우
                                SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd'T'hhmmss");
                                SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                SimpleDateFormat new_format2 = new SimpleDateFormat("yyyyMMdd");
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                                original_format.setTimeZone(timeZone);

                                Date original_date = original_format.parse(dtEnd);
                                dtEnd = new_format.format(original_date);

                                icsMap.put("END_DAY", new_format2.format(original_date));
                                icsMap.put("end", dtEnd);
                            } else if (dtEnd.length() == 16) { // 데이트 포맷 Z로 끝나는 경우
                                SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
                                SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                SimpleDateFormat new_format2 = new SimpleDateFormat("yyyyMMdd");
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                                original_format.setTimeZone(timeZone);

                                Date original_date = original_format.parse(dtEnd);
                                dtEnd = new_format.format(original_date);

                                icsMap.put("END_DAY", new_format2.format(original_date));
                                icsMap.put("end", dtEnd);
                            }
                        }
                        // 심윤영 - ㄲ

                        continue;
                    }

                    if (str.matches(".*DESCRIPTION.*")) {
                        description = str;
                        description = description.substring(description.indexOf(":") + 1);

                        // icsMap.put("DESCRIPTION", description);
                        icsMap.put("EVENT_DETAIL", description);
                        continue;
                    }
                    if (str.matches(".*LOCATION.*")) {
                        location = str;
                        location = location.substring(location.indexOf(":") + 1);

                        icsMap.put("LOCATION", location);
                        continue;
                    }
                    if (str.matches(".*SUMMARY.*")) {
                        summary = str;
                        summary = summary.substring(summary.indexOf(":") + 1);

                        // icsMap.put("SUMMARY", summary);
                        icsMap.put("title", summary);
                    }

                    if (str.matches(".*RRULE.*")) {
                        rrule = str;
                        rrule = rrule.substring(rrule.indexOf(":") + 1);

                        String endRuleType = "";

                        if (rrule.matches(".*UNTIL=.*")) {
                            endRuleType = "until";

                            String endRule = rrule.substring(rrule.indexOf("UNTIL=") + 6);
                            endRule = endRule.substring(0, endRule.indexOf(";"));

                            SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd");
                            SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd");
                            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                            original_format.setTimeZone(timeZone);

                            Date original_date = original_format.parse(endRule);
                            endRule = new_format.format(original_date);

                            icsMap.put("RECURRENCE_END_DATE", endRule);
                        } else if (rrule.matches(".*COUNT=.*")) {
                            endRuleType = "count";

                            String endRule = rrule.substring(rrule.indexOf("COUNT=") + 6);
                            endRule = endRule.substring(0, endRule.indexOf(";"));

                            icsMap.put("RECURRENCE_COUNT", endRule);
                        } else {
                            endRuleType = "loop";
                        }
                        icsMap.put("END_RULE", endRuleType);
                        icsMap.put("RECURRENCE_RULE", rrule);
                        icsMap.put("REPEAT_YN", "Y");
                    }
                    // 심 수정중
                    if (str.matches(".*EXDATE.*")) {
                        String orDate = str;
                        orDate = orDate.substring(orDate.indexOf(":") + 1);

                        SimpleDateFormat original_format = new SimpleDateFormat("yyyyMMdd");
                        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd");

                        Date original_date = original_format.parse(orDate);
                        orDate = new_format.format(original_date);

                        if (exDate != null && exDate != "") {
                            exDate += "," + orDate;
                        } else {
                            exDate = orDate;
                        }
                        // 하단 END:VEVENT에서 동일한 uid가 있는지 체크 후 맵에 담음.
                    }

                    if (str.matches("END:VEVENT")) {

                        if (icsMap.get("REPEAT_YN") == null) {
                            icsMap.put("REPEAT_YN", "N");
                        }

                        if (rrule != null && rrule != "") {
                            for (int i = 0; i < recurList.size(); i++) {
                                if (recurList.get(i).get("uid").toString().equals(uid)) {
                                    if (exDate != null && exDate != "") {
                                        exDate += "," + recurList.get(i).get("recur_id").toString();
                                    } else {
                                        exDate = recurList.get(i).get("recur_id").toString();
                                    }
                                }
                                recurList.remove(i);
                                i = i - 1;
                            }
                            icsMap.put("EX_DATE", exDate);
                        }

                        list.add(icsMap);
                        icsMap = new HashMap<String, Object>();
                    }
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 샐러스 캘린더를 ICS 파일로 만들기.
     * 
     * @param list
     * @param map
     * @throws Exception
     */
    public void outIcsEventList(List<Map<String, Object>> list, Map<String, Object> map) throws Exception {

        log.debug("outIcsEventList Call.");

        String beginVcal = "BEGIN:VCALENDAR\n";
        String prodid = "PRODID:-//Unipoint Corporation//sellers MIMEDIR//EN\n";
        String version = "VERSION:2.0\n";
        String method = "METHOD:PUBLISH\n";

        String beginVtime = "BEGIN:VTIMEZONE\n";
        String tzid = "TZID:Korea Standard Time\n";

        String beginStandard = "BEGIN:STANDARD\n";
        String tzoffSetFrom = "TZOFFSETFROM:+0900\n";
        String tzoffSetTo = "TZOFFSETTO:+0900\n";
        String endStandard = "END:STANDARD\n";
        String endVtimeZone = "END:VTIMEZONE\n";

        // body start
        String eventBegin = "BEGIN:VEVENT\n";
        String classPublic = "CLASS:PUBLIC\n";

        String location = "LOCATION:\n";
        String sequence = "SEQUENCE:0\n";
        String status = "STATUS:CONFIRMED\n";

        String tranSp = "TRANSP:QPAQUE\n";
        String eventEnd = "END:VEVENT\n";
        // end

        String calEnd = "END:VCALENDAR";

        try {

            String creatorId = (String) map.get("creatorId");
            String filePathThisCreatorId = calUserInfo + "google/out/" + "." + creatorId + ".ics";

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(bos));

            fw.write(beginVcal);
            fw.write(prodid);
            fw.write(version);
            fw.write(method);
            fw.write(beginVtime);
            fw.write(tzid);
            fw.write(beginStandard);
            fw.write(tzoffSetFrom);
            fw.write(tzoffSetTo);
            fw.write(endStandard);
            fw.write(endVtimeZone);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String startDate = "DTSTART:";
                    String endDate = "DTEND:";
                    String created = "CREATED:";
                    String description = "DESCRIPTION:";
                    String lastModified = "LAST-MODIFIED:";
                    String summary = "SUMMARY:";
                    String repeatRule = "RRULE:";

                    SimpleDateFormat new_format = new SimpleDateFormat("yyyyMMddkkmmss");
                    String new_date = new_format.format(list.get(i).get("START_DATETIME"));
                    String yymmdd = new_date.substring(0, 8);
                    String kkmmss = new_date.substring(8, 14);
                    String startDateTime = yymmdd + "T" + kkmmss;

                    // startDate += list.get(i).get("START_DATETIME");
                    startDate += startDateTime;
                    endDate += list.get(i).get("END_DATETIME");
                    created += list.get(i).get("SYS_REGISTER_DATE");
                    description += list.get(i).get("EVENT_DETAIL");
                    lastModified += list.get(i).get("SYS_UPDATE_DATE");
                    summary += list.get(i).get("EVENT_SUBJECT");

                    fw.write(eventBegin);
                    fw.write(classPublic);

                    fw.write(startDate + "\n");
                    fw.write(endDate + "\n");
                    fw.write(created + "\n");
                    if (list.get(i).get("REPEAT_YN").equals("Y")) {
                        repeatRule += list.get(i).get("RECURRENCE_RULE") + "\n";
                        fw.write(repeatRule);
                    }
                    fw.write(description + "\n");
                    fw.write(lastModified + "\n");

                    fw.write(location);
                    fw.write(sequence);
                    fw.write(status);

                    fw.write(summary + "\n");

                    fw.write(tranSp);
                    fw.write(eventEnd);

                    fw.flush();

                }
            }

            fw.write(calEnd);
            fw.flush();

            String url = s3FileUtils.upload(
                    bos.toByteArray(),
                    filePathThisCreatorId,
                    "text/*");

            log.info("upload url ::" + url);

            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 사내캘린더 연동 로그인시, ID/PASSWORD 잘못되었을때, 로그인 파일 삭제.
     * 
     * @param filePath
     */
    public void deleteIncorrectUserLoginInfo(String filePath) {
        s3FileUtils.delete(filePath);
    }

    /**
     * 아웃룩 로그인 ID, PASSWORD가 담긴 파일이 있는지 Check 암호화된 파일은 복호화 한 후에 ID, PASSWORD
     * 데이터를 가져온다. ID, PASSWORD 값을 획득한다.
     * 
     * @param map
     * @param list
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> readOutlookInfoFile(Map<String, Object> map, List<Map<String, Object>> list,
            String calType, String userFilePath)
            throws Exception {

        log.debug("readOutlookInfoFile Call.");

        String serverUrl = "outlook/";
        String fileDir = "";
        String creatorId = (String) map.get("MEMBER_ID_NUM");
        Map<String, Object> outlookMap = new HashMap<String, Object>();

        serverUrl = calType + "/";

        fileDir = userFilePath + serverUrl + "." + creatorId + ".txt"; // 암호화된파일(복호화시킬파일)

        outlookMap.put("userLoginInfoFilePath", fileDir);

        byte[] fileByte = s3FileUtils.download(fileDir);

        if (fileByte != null && fileByte.length > 0) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            InputStreamReader ir = new InputStreamReader(new ByteArrayInputStream(fileByte)); // 복호화 문서 파일 내용을 읽어 온다.

            int secret = 3;
            int data = 0;
            log.info("::Decryption Start");
            while ((data = ir.read()) != -1) {
                data -= secret; // 복호화
                bos.write(data); // 복호화된 데이터를 저장한다.
            }
            ir.close();
            log.info(" ::Decryption End");

            String line = null;
            String str = "";
            String outlookId = "";
            String outlookPw = "";
            String creatorNum = "";
            String serverNm = "";

            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos.toByteArray())));

            while ((line = in.readLine()) != null) {
                str = line;
                log.info(str);
                if (str.matches(".*outlookId.*")) {
                    outlookId = str;
                    outlookId = outlookId.substring(outlookId.indexOf(":") + 1);

                    outlookMap.put("outlookId", outlookId);
                }

                if (str.matches(".*outlookPw.*")) {
                    outlookPw = str;
                    outlookPw = outlookPw.substring(outlookPw.indexOf(":") + 1);

                    outlookMap.put("outlookPw", outlookPw);
                }

                if (str.matches(".*creatorId.*")) {
                    creatorNum = str;
                    creatorNum = creatorNum.substring(creatorNum.indexOf(":") + 1);

                    outlookMap.put("creatorId", creatorNum);
                }

                if (str.matches(".*serverNm.*")) {
                    serverNm = str;
                    serverNm = serverNm.substring(serverNm.indexOf(":") + 1);

                    outlookMap.put("serverNm", serverNm);
                }
            }

            list.add(outlookMap);
            in.close();
            bos.close();
        } else {
            // 스크립트에서 분기하기위한 상태값
            log.debug("Outlook Login Fail");
            outlookMap.put("errStatus", "2");
            outlookMap.put("errMsg", "사내캘린더 일정 가져오기를 실패하였습니다. \npc에서 다시 로그인 하십시오.");
            list.add(outlookMap);
        }

        return list;
    }

    /**
     * 아웃룩 ID, PASSWORD 를 .txt 파일로 저장한 후 파일 내용을 암호화 한다.
     * 
     * @param map
     * @param outlookID
     * @param outlookPW
     * @throws Exception
     */
    public void outlookInfoSave(String creatorId, String outlookID, String outlookPW, String flag, String userFilepath)
            throws Exception {

        log.debug("outlookInfoSave Call. ");

        String outlookId = "outlookId:";
        String outlookPw = "outlookPw:";
        String creatorNum = "creatorId:";
        String serverName = "serverNm:";
        String exServer = "";
        if (flag.equals("outlook")) {
            exServer = flag + "/";
            serverName += flag;
        } else if (flag.equals("office365")) {
            exServer = flag + "/";
            serverName += flag;
        }
        String encrypFile = userFilepath + exServer + "." + creatorId + ".txt";

        try {
            outlookId += outlookID + "\r\n";
            outlookPw += outlookPW + "\r\n";
            creatorNum += creatorId + "\r\n";

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(bos));

            // 파일안에 문자열 쓰기
            fw.write(outlookId);
            fw.write(outlookPw);
            fw.write(creatorNum);
            fw.write(serverName);
            fw.flush();

            // 객체 닫기
            fw.close();

            // =================== 파일 암호화===================================
            InputStreamReader ir = new InputStreamReader(new ByteArrayInputStream(bos.toByteArray()));
            ByteArrayOutputStream encBos = new ByteArrayOutputStream();
            // OutputStreamWriter ow = new OutputStreamWriter(encBos);
            int secret = 3; // 암호화&복호화 해주려는 값
            int data = 0;

            log.debug("File Encryption Start");
            while ((data = ir.read()) != -1) {
                data += secret; // 암호화
                encBos.write(data); // 암호화된 데이터를 저장한다.
            } // while

            log.debug("File Encryption End");
            ir.close();
            bos.close();

            // S3 upload
            String url = s3FileUtils.upload(
                    encBos.toByteArray(),
                    encrypFile,
                    "text/*");

            log.info("upload url ::" + url);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
