package com.lionking.ddingchun.crawler.config;

import java.util.List;

public final class CrawlTargets {

    private CrawlTargets() {
    }

    public static final List<SiteConfig> SITES = List.of(

            new SiteConfig("https://www.mju.ac.kr", "mjukr", "141",
                    "명지대 메인 - 일반공지", true, "NOTICE", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "142",
                    "명지대 메인 - 행사공지", true, "EVENT", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "143",
                    "명지대 메인 - 학사공지", true, "NOTICE", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "262",
                    "명지대 메인 - 학사일정", true, "NOTICE", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "145",
                    "명지대 메인 - 장학/학자금공지", true, "SCHOLARSHIP", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "146",
                    "명지대 메인 - 진로/취업/창업공지", true, "CAREER", "ALL"),
            new SiteConfig("https://www.mju.ac.kr", "mjukr", "147",
                    "명지대 메인 - 입찰공지", true, "NOTICE", "ALL"),
            new SiteConfig("https://eciems.mju.ac.kr", "ctl", "5259",
                    "교수학습센터 - 공지사항", true, "NOTICE", "ALL"),

            new SiteConfig("https://innov.mju.ac.kr", "innovation", "56",
                    "대학혁신지원사업단 - 공지사항", true, "CONTEST", "ALL"),
            new SiteConfig("https://innov.mju.ac.kr", "innovation", "67",
                    "대학혁신지원사업단 - 보도자료", true, "NOTICE", "ALL"),

            new SiteConfig("https://dcd.mju.ac.kr", "dcd", "1118",
                    "디지털콘텐츠디자인학과 - 학사공지", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://dcd.mju.ac.kr", "dcd", "1117",
                    "디지털콘텐츠디자인학과 - 학과공지", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://ict.mju.ac.kr", "ict", "9829",
                    "인공지능소프트웨어융합대학 - 공지사항", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://humanities.mju.ac.kr", "humanities", "2801",
                    "인문대학 - 공지사항", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://www.mju.ac.kr", "social", "2940",
                    "사회과학대학 - 공지사항", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://www.mju.ac.kr", "sba", "2223",
                    "경영대학- 공지사항", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://mhl.mju.ac.kr", "mhl", "11480",
                    "미디어휴만라이프대학- 공지사항", true, "NOTICE", "HUMANITIES"),
            new SiteConfig("https://www.mju.ac.kr", "bangmok", "1682",
                    "방목기초교육대학- 공지사항", true, "NOTICE", "HUMANITIES"),

            new SiteConfig("https://scict.mju.ac.kr", "scict", "10494",
                    "반도체ICT대학 - 공지사항", true, "NOTICE", "NATURAL"),
            new SiteConfig("https://www.mju.ac.kr", "nature", "3004",
                    "화학생명과학대학- 공지사항", true, "NOTICE", "NATURAL"),
            new SiteConfig("https://www.mju.ac.kr", "eng", "1436",
                    "스마트시스템공과대학 - 공지사항", true, "NOTICE", "NATURAL"),
            new SiteConfig("https://www.mju.ac.kr", "bangmok", "1681",
                    "방목기초교육대학 - 공지사항", true, "NOTICE", "NATURAL"),

            new SiteConfig("https://www.mju.ac.kr", "mjukr", "10235",
                    "아너칼리지대학 - 공지사항", true, "NOTICE", "ALL")

            );
}