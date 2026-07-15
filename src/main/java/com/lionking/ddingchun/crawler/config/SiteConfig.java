package com.lionking.ddingchun.crawler.config;

/**
 *
 * baseUrl     : 사이트 도메인 (끝에 / 없이) 예) "https://www.mju.ac.kr"
 * siteId      : 주소의 /bbs/{siteId}/... 부분             예) "mjukr"
 * bbsId       : 주소의 /bbs/.../{bbsId}/... 부분 (게시판 번호) 예) "141"
 * sourceName  : 결과에 표시할 이름
 * useRss      : true면 RSS로, false면 HTML 표를 직접 읽음
 * category    : NOTICE/EVENT/CONTEST/SCHOLARSHIP/CLUB/FESTIVAL/EXHIBITION/CAREER
 * campus      : HUMANITIES/NATURAL/ALL
 *
 */

public record SiteConfig(
        String baseUrl,
        String siteId,
        String bbsId,
        String sourceName,
        boolean useRss,
        String category,
        String campus
) {
    public String rssUrl(int row) {
        return baseUrl + "/bbs/" + siteId + "/" + bbsId + "/rssList.do?row=" + row;
    }

    public String listUrl() {
        return baseUrl + "/bbs/" + siteId + "/" + bbsId + "/artclList.do?layout=unknown";
    }
}
