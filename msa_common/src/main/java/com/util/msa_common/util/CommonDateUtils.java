package com.per.msa_common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CommonDateUtils {
    /**
     * 현재날짜 가져오기. 기본포맷적용(yyyy-MM-dd)
     * 
     * @return 현재일자 yyyy-MM-dd형식
     */
    public static String getToday() {
        return getToday("yyyy-MM-dd");
    }

    public static String getTodayFormat() {
        return getToday("yyyyMMdd");
    }

    public static String getTodayDateTime() {
        return getToday("yyyy-MM-dd HH:mm");
    }

    /**
     * 입력받은 형식에 맞는 현재날짜(시간) 가져오기
     * 
     * @param fmt 날짜형식
     * @return 현재일자
     */
    public static String getToday(String fmt) {
        SimpleDateFormat sfmt = new SimpleDateFormat(fmt);
        return sfmt.format(new Date());
    }

    /**
     * ****년 **월 **일 **시 **분 포멧.
     * 
     * @param fmt
     * @return
     */
    public static String getTodayFormatKorean() {
        SimpleDateFormat sfmt = new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분");
        return sfmt.format(new Date());
    }

    /**
     * Date형을 yyyy-MM-dd형의 String으로 변환.
     * 
     * @param date 날짜 Date형
     * @return 날짜 String형. null일 경우 null return
     */
    public static String dateToString(Date date) {
        if (date != null)
            return dateToString(date, "yyyy-MM-dd");
        else
            return null;
    }

    public static String dateToString2(Date date) {
        if (date != null)
            return dateToString(date, "yyyy-MM-dd HH:mm");
        else
            return null;
    }

    /**
     * Date형을 원하는 포맷으로 변환하여 스트링으로 전환한다.
     * 
     * @param date 날짜 Date형
     * @return 날짜 String형. null일 경우 null return
     */
    public static String dateToString(Date date, String fmt) {
        if (date != null && fmt != null) {
            SimpleDateFormat sfmt = new SimpleDateFormat(fmt);
            return sfmt.format(date);
        } else
            return null;
    }

    /**
     * 특정 Format의 String을 Date로 변환
     * 
     * @param date 날짜 String형
     * @param fmt  날짜 String형의 Format
     * @return 날짜 Date형. 날짜 String형의 오류가 있을 경우 null return
     */
    public static java.util.Date stringToDate(String date, String fmt) {

        if (date != null && fmt != null) {
            SimpleDateFormat sfmt = new SimpleDateFormat(fmt);
            try {
                return sfmt.parse(date);
            } catch (ParseException pe) {
                return null;
            }
        } else
            return null;
    }

    /**
     * java.util.Date를 java.sql.Date로 변환
     */
    public static java.sql.Date dateToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static String getTimeStamp() {
        return getTimeStamp(1);
    }

    public static String getTimeStamp(int iMode) {
        String sFormat;
        // if (iMode == 1) sFormat = "E MMM dd HH:mm:ss z yyyy"; // Wed Feb 03 15:26:32
        // GMT+09:00 1999
        if (iMode == 1)
            sFormat = "MMMM dd, yyyy HH:mm:ss z"; // Jun 03, 2001 15:26:32 GMT+09:00
        else if (iMode == 2)
            sFormat = "MM/dd/yyyy";// 02/15/1999
        else if (iMode == 3)
            sFormat = "yyyyMMdd";// 19990215
        else if (iMode == 4)
            sFormat = "HHmmss";// 121241
        else if (iMode == 5)
            sFormat = "dd MMM yyyy";// 15 Jan 1999
        else if (iMode == 6)
            sFormat = "yyyyMMddHHmm"; // 200101011010
        else if (iMode == 7)
            sFormat = "yyyyMMddHHmmss"; // 20010101101052
        else if (iMode == 8)
            sFormat = "HHmmss";
        else if (iMode == 9)
            sFormat = "yyyy";
        else if (iMode == 10)
            sFormat = "MM";

        else
            sFormat = "E MMM dd HH:mm:ss z yyyy";// Wed Feb 03 15:26:32 GMT+09:00 1999

        Locale locale = new Locale("en", "EN");
        // SimpleTimeZone timeZone = new SimpleTimeZone(32400000, "KST");
        SimpleDateFormat formatter = new SimpleDateFormat(sFormat, locale);
        // formatter.setTimeZone(timeZone);
        // SimpleDateFormat formatter = new SimpleDateFormat(sFormat);

        return formatter.format(new Date());
    }

    public static int quarterYear(int month) {
        return (int) Math.ceil(month / 3.0);
    }

    public static String getDate() {
        // return getDate(0);
        return getTimeStamp(3);
    }

    /**
     * 오늘 날짜에서 지정한 날수를 계산한 날짜 반환
     */
    public static String getDate(int i) {
        return getDate(1, null, i);

    }

    /**
     * 지정한 날짜에서 지정한 날수를 계산한 날짜 반환
     */
    public static String getDate(String sDate, int i) {
        return getDate(1, sDate, i);
    }

    /**
     * 지정한 날짜에서 지정한 날수를 계산한 날짜 반환 <br>
     * 
     * @param iType 앞/뒤로 계산할 단위 (1:일 단위, 2:월 단위, 3:년 단위)
     * @param sDate 기준이 되는 날짜 - null일 경우, 오늘 날짜를 기준
     * @param i     앞/뒤로 증가/감소 시킬 수
     */
    public static String getDate(int iType, String sDate, int i) {

        if (sDate == null)
            sDate = getTimeStamp(3);

        if (i == 0)
            return sDate;
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(sDate.substring(0, 4)), Integer.parseInt(sDate.substring(4, 6)) - 1,
                    Integer.parseInt(sDate.substring(6, 8)));

            if (iType == 2)
                cal.add(Calendar.MONTH, i); // 월 단위
            else if (iType == 3)
                cal.add(Calendar.YEAR, i); // 년 단위
            else
                cal.add(Calendar.DATE, i); // 일 단위

            int iYear = cal.get(Calendar.YEAR);
            int iMonth = cal.get(Calendar.MONTH) + 1;
            int iDate = cal.get(Calendar.DATE);

            String sNewDate = "" + iYear;
            if (iMonth < 10)
                sNewDate += "0" + iMonth;
            else
                sNewDate += iMonth;
            if (iDate < 10)
                sNewDate += "0" + iDate;
            else
                sNewDate += iDate;

            return sNewDate;
        }
    }

    /**
     * 오늘 날짜에서 지정한 날수를 계산한 날짜 반환 <br>
     * 
     * @param iType   앞/뒤로 계산할 단위 (1:일 단위, 2:월 단위, 3:년 단위)
     * @param i       앞/뒤로 증가/감소 시킬 수
     * @param dFormat 날짜 포맷
     */
    public static String getDate(int iType, int i, String dFormat) {

        if (i == 0)
            return getToday(dFormat);
        else {
            Calendar cal = Calendar.getInstance();

            if (iType == 2)
                cal.add(Calendar.MONTH, i); // 월 단위
            else if (iType == 3)
                cal.add(Calendar.YEAR, i); // 년 단위
            else
                cal.add(Calendar.DATE, i); // 일 단위

            SimpleDateFormat sdf = new SimpleDateFormat(dFormat);
            String sNewDate = sdf.format(cal.getTime());

            return sNewDate;
        }
    }

    /**
     * 오늘 날짜보다 일주일 전 날짜를 반환한다.
     */
    public static String getPreviousWeek() {
        return getDate(1, null, -7);
    }

    /**
     * 오늘 날짜보다 한달 전 날짜를 반환한다.
     */
    public static String getPreviousMonth() {
        return getDate(2, null, -1);
    }

    /**
     * 해당날짜에서 몇시간전 날짜 및 시간 <br>
     * 
     * @param i     감소할 시간 수 (ex:1,..24 =>1시간전,1일전..)
     * @param sDate 날짜 (yyyyMMddHHmm)
     */
    public String getDateBeforeTime(String sDate, int i) {
        Date dt = stringToDate(sDate, "yyyyMMddHHmm");
        String BeforeDate = "";

        if (sDate == null) {
            BeforeDate = sDate;
        } else {
            Date preDate = new Date();
            preDate.setTime(dt.getTime() - ((long) 1000 * 60 * 60 * i));

            BeforeDate = dateToString(preDate, "yyyyMMddHHmm");
        }

        return BeforeDate;
    }

    /**
     * 해당날짜에서 몇시간전 날짜 및 분 <br>
     * 
     * @param i     감소할 분 수 (ex:1,..60 =>1분전,60분전..)
     * @param sDate 날짜 (yyyyMMddHHmm)
     */
    public static String getDateBeforeTimeMinute(String sDate, int i) {
        Date dt = stringToDate(sDate, "yyyy-MM-dd HH:mm");
        String BeforeDate = "";

        if (sDate == null) {
            BeforeDate = sDate;
        } else {
            Date preDate = new Date();
            preDate.setTime(dt.getTime() - ((long) 1000 * 60 * i));

            BeforeDate = dateToString(preDate, "yyyy-MM-dd HH:mm");
        }

        return BeforeDate;
    }

    public static String getDateAfterTimeMinute(String sDate, int i) {
        Date dt = stringToDate(sDate, "yyyy-MM-dd HH:mm");
        String BeforeDate = "";

        if (sDate == null) {
            BeforeDate = sDate;
        } else {
            Date preDate = new Date();
            preDate.setTime(dt.getTime() + ((long) 1000 * 60 * i));

            BeforeDate = dateToString(preDate, "yyyy-MM-dd HH:mm");
        }

        return BeforeDate;
    }

    /**
     * 날짜형식 : 2009년 10월 26일 오전 1:24<br>
     * 
     * @param sDate 날짜 (yyyyMMddHHmm)
     */
    public static String getDateKoreaStr(String sDate) {
        SimpleDateFormat formatter;
        String pattern = "yyyy년 M월 d일  a h:mm";
        String result;

        formatter = new SimpleDateFormat(pattern, new Locale("ko", "KOREA"));

        Date dt = stringToDate(sDate, "yyyyMMddHHmm");
        result = formatter.format(dt);

        return result;
    }

    // 밀리세컨드 -> 분 변경.
    public static long getMillisecondToMin() {
        long millisecond = System.currentTimeMillis();
        return (millisecond / (1000 * 60)) % 60;
    }

    /**
     * 오늘 날짜에서 Cycle 숫자 후의 날짜
     * 
     * @param beforeDueDate
     * @return
     * @throws Exception
     */
    public static String getBeforeDueDate(String beforeDueDate) throws Exception {
        // 오늘날짜에서 하루, 이틀, 일주일 등 후 날짜.
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, Integer.parseInt(beforeDueDate));
        SimpleDateFormat to = new SimpleDateFormat("yyyy-MM-dd");
        String afterDay = to.format(cal.getTime());

        return afterDay;
    }

    /**
     * 오늘날짜 - 해결마감일 ( 날짜 차이 계산 )
     * 
     * @param dueDate
     * @param todayDate
     * @return
     * @throws Exception
     */
    public static long getCalDateDays(String dueDate, String todayDate) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDateFormat = format.parse(dueDate); // 해결 목표일
        Date todayDateFormat = format.parse(todayDate); // 오늘날짜
        long calDate = todayDateFormat.getTime() - dueDateFormat.getTime(); // 오늘날짜 - 해결 목표일
        long calDateDays = calDate / (24 * 60 * 60 * 1000); // 날짜 차이

        return calDateDays;
    }

    /**
     * 요일 구하기.
     * 
     * @param date
     * @return
     */
    public String getDateDay(String date) {

        String day = date;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date nDate = null;
        try {
            nDate = dateFormat.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }
}
