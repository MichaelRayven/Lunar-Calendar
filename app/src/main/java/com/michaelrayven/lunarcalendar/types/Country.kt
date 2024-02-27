package com.michaelrayven.lunarcalendar.types

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: String,
    val code: String,
) {
    fun getByCode(code: String): Country? {
        return COUNTRY_LIST.find {
            it.code == code
        }
    }

    companion object {
        val COUNTRY_LIST = listOf(
            Country(
                code = "RU",
                name = "Россия"
            ),
            Country(
                code = "AZ",
                name = "Азербайджан"
            ),
            Country(
                code = "AM",
                name = "Армения"
            ),
            Country(
                code = "BY",
                name = "Беларусь"
            ),
            Country(
                code = "GE",
                name = "Грузия"
            ),
            Country(
                code = "KZ",
                name = "Казахстан"
            ),
            Country(
                code = "MD",
                name = "Молдова"
            ),
            Country(
                code = "TJ",
                name = "Таджикистан"
            ),
            Country(
                code = "TM",
                name = "Туркменистан"
            ),
            Country(
                code = "UZ",
                name = "Узбекистан"
            ),
            Country(
                code = "UA",
                name = "Украина"
            ),
            Country(
                code = "AU",
                name = "Австралия"
            ),
            Country(
                code = "AT",
                name = "Австрия"
            ),
            Country(
                code = "AZ",
                name = "Азербайджан"
            ),
            Country(
                code = "AL",
                name = "Албания"
            ),
            Country(
                code = "DZ",
                name = "Алжир"
            ),
            Country(
                code = "AO",
                name = "Ангола"
            ),
            Country(
                code = "AR",
                name = "Аргентина"
            ),
            Country(
                code = "AM",
                name = "Армения"
            ),
            Country(
                code = "AF",
                name = "Афганистан"
            ),
            Country(
                code = "BS",
                name = "Багамские острова"
            ),
            Country(
                code = "BD",
                name = "Бангладеш"
            ),
            Country(
                code = "BB",
                name = "Барбадос"
            ),
            Country(
                code = "BH",
                name = "Бахрейн"
            ),
            Country(
                code = "BY",
                name = "Беларусь"
            ),
            Country(
                code = "BZ",
                name = "Белиз"
            ),
            Country(
                code = "BE",
                name = "Бельгия"
            ),
            Country(
                code = "BJ",
                name = "Бенин"
            ),
            Country(
                code = "BG",
                name = "Болгария"
            ),
            Country(
                code = "BO",
                name = "Боливия"
            ),
            Country(
                code = "BA",
                name = "Босния"
            ),
            Country(
                code = "BW",
                name = "Ботсвана"
            ),
            Country(
                code = "BR",
                name = "Бразилия"
            ),
            Country(
                code = "BN",
                name = "Бруней Даруссалам"
            ),
            Country(
                code = "BF",
                name = "Буркина Фасо"
            ),
            Country(
                code = "BI",
                name = "Бурунди"
            ),
            Country(
                code = "BT",
                name = "Бутан"
            ),
            Country(
                code = "GB",
                name = "Великобритания"
            ),
            Country(
                code = "HU",
                name = "Венгрия"
            ),
            Country(
                code = "VE",
                name = "Венесуэла"
            ),
            Country(
                code = "TL",
                name = "Восточный Тимор"
            ),
            Country(
                code = "VN",
                name = "Вьетнам"
            ),
            Country(
                code = "GA",
                name = "Габон"
            ),
            Country(
                code = "HT",
                name = "Гаити"
            ),
            Country(
                code = "GY",
                name = "Гайана"
            ),
            Country(
                code = "GM",
                name = "Гамбия"
            ),
            Country(
                code = "GH",
                name = "Гана"
            ),
            Country(
                code = "GP",
                name = "Гваделупа"
            ),
            Country(
                code = "GT",
                name = "Гватемала"
            ),
            Country(
                code = "GN",
                name = "Гвинея"
            ),
            Country(
                code = "GW",
                name = "Гвинея-Биссау"
            ),
            Country(
                code = "DE",
                name = "Германия"
            ),
            Country(
                code = "HN",
                name = "Гондурас"
            ),
            Country(
                code = "HK",
                name = "Гонконг"
            ),
            Country(
                code = "GR",
                name = "Греция"
            ),
            Country(
                code = "GE",
                name = "Грузия"
            ),
            Country(
                code = "DK",
                name = "Дания"
            ),
            Country(
                code = "DJ",
                name = "Джибути"
            ),
            Country(
                code = "DO",
                name = "Доминиканская Республика"
            ),
            Country(
                code = "EG",
                name = "Египет"
            ),
            Country(
                code = "ZM",
                name = "Замбия"
            ),
            Country(
                code = "ZW",
                name = "Зимбабве"
            ),
            Country(
                code = "IL",
                name = "Израиль"
            ),
            Country(
                code = "IN",
                name = "Индия"
            ),
            Country(
                code = "ID",
                name = "Индонезия"
            ),
            Country(
                code = "JO",
                name = "Иордания"
            ),
            Country(
                code = "IQ",
                name = "Ирак"
            ),
            Country(
                code = "IR",
                name = "Иран"
            ),
            Country(
                code = "IE",
                name = "Ирландия"
            ),
            Country(
                code = "IS",
                name = "Исландия"
            ),
            Country(
                code = "ES",
                name = "Испания"
            ),
            Country(
                code = "IT",
                name = "Италия"
            ),
            Country(
                code = "YE",
                name = "Йемен"
            ),
            Country(
                code = "KZ",
                name = "Казахстан"
            ),
            Country(
                code = "KH",
                name = "Камбоджа"
            ),
            Country(
                code = "CM",
                name = "Камерун"
            ),
            Country(
                code = "CA",
                name = "Канада"
            ),
            Country(
                code = "QA",
                name = "Катар"
            ),
            Country(
                code = "KE",
                name = "Кения"
            ),
            Country(
                code = "CY",
                name = "Кипр"
            ),
            Country(
                code = "CN",
                name = "Китай"
            ),
            Country(
                code = "CO",
                name = "Колумбия"
            ),
            Country(
                code = "KM",
                name = "Коморские о-ва"
            ),
            Country(
                code = "CG",
                name = "Конго"
            ),
            Country(
                code = "CD",
                name = "Конго, Демократическая Республика"
            ),
            Country(
                code = "XK",
                name = "Косово"
            ),
            Country(
                code = "CR",
                name = "Коста-Рика"
            ),
            Country(
                code = "CI",
                name = "Кот д’Ивуар"
            ),
            Country(
                code = "CU",
                name = "Куба"
            ),
            Country(
                code = "KW",
                name = "Кувейт"
            ),
            Country(
                code = "KG",
                name = "Кыргызстан"
            ),
            Country(
                code = "LA",
                name = "Лаос"
            ),
            Country(
                code = "LV",
                name = "Латвия"
            ),
            Country(
                code = "LS",
                name = "Лесото"
            ),
            Country(
                code = "LR",
                name = "Либерия"
            ),
            Country(
                code = "LB",
                name = "Ливан"
            ),
            Country(
                code = "LY",
                name = "Ливия"
            ),
            Country(
                code = "LT",
                name = "Литва"
            ),
            Country(
                code = "LU",
                name = "Люксембург"
            ),
            Country(
                code = "MU",
                name = "Маврикий"
            ),
            Country(
                code = "MR",
                name = "Мавритания"
            ),
            Country(
                code = "MG",
                name = "Мадагаскар"
            ),
            Country(
                code = "MO",
                name = "Макао"
            ),
            Country(
                code = "MK",
                name = "Македония"
            ),
            Country(
                code = "MW",
                name = "Малави"
            ),
            Country(
                code = "MY",
                name = "Малайзия"
            ),
            Country(
                code = "ML",
                name = "Мали"
            ),
            Country(
                code = "MV",
                name = "Мальдивские о-ва"
            ),
            Country(
                code = "MT",
                name = "Мальта"
            ),
            Country(
                code = "MA",
                name = "Марокко"
            ),
            Country(
                code = "MQ",
                name = "Мартиника"
            ),
            Country(
                code = "MX",
                name = "Мексика"
            ),
            Country(
                code = "MZ",
                name = "Мозамбик"
            ),
            Country(
                code = "MD",
                name = "Молдова"
            ),
            Country(
                code = "MN",
                name = "Монголия"
            ),
            Country(
                code = "MM",
                name = "Мьянма"
            ),
            Country(
                code = "NA",
                name = "Намибия"
            ),
            Country(
                code = "NP",
                name = "Непал"
            ),
            Country(
                code = "NE",
                name = "Нигер"
            ),
            Country(
                code = "NG",
                name = "Нигерия"
            ),
            Country(
                code = "AN",
                name = "Нидерландские Антильские о."
            ),
            Country(
                code = "NL",
                name = "Нидерланды"
            ),
            Country(
                code = "NI",
                name = "Никарагуа"
            ),
            Country(
                code = "NZ",
                name = "Новая Зеландия"
            ),
            Country(
                code = "NO",
                name = "Норвегия"
            ),
            Country(
                code = "AE",
                name = "Объединенные Арабские Эмираты"
            ),
            Country(
                code = "OM",
                name = "Оман"
            ),
            Country(
                code = "CV",
                name = "Острова Зеленого Мыса"
            ),
            Country(
                code = "PK",
                name = "Пакистан"
            ),
            Country(
                code = "PS",
                name = "Палестинские территории"
            ),
            Country(
                code = "PA",
                name = "Панама"
            ),
            Country(
                code = "PG",
                name = "Папуа – Новая Гвинея"
            ),
            Country(
                code = "PY",
                name = "Парагвай"
            ),
            Country(
                code = "PE",
                name = "Перу"
            ),
            Country(
                code = "PL",
                name = "Польша"
            ),
            Country(
                code = "PT",
                name = "Португалия"
            ),
            Country(
                code = "PR",
                name = "Пуэрто-Рико"
            ),
            Country(
                code = "RE",
                name = "Реюньон"
            ),
            Country(
                code = "RU",
                name = "Россия"
            ),
            Country(
                code = "RW",
                name = "Руанда"
            ),
            Country(
                code = "RO",
                name = "Румыния"
            ),
            Country(
                code = "SV",
                name = "Сальвадор"
            ),
            Country(
                code = "SA",
                name = "Саудовская Аравия"
            ),
            Country(
                code = "SZ",
                name = "Свазиленд"
            ),
            Country(
                code = "KP",
                name = "Северная Корея"
            ),
            Country(
                code = "SN",
                name = "Сенегал"
            ),
            Country(
                code = "RS",
                name = "Сербия"
            ),
            Country(
                code = "CS",
                name = "Сербия и Черногория"
            ),
            Country(
                code = "SG",
                name = "Сингапур"
            ),
            Country(
                code = "SY",
                name = "Сирийская Арабская Республика"
            ),
            Country(
                code = "SK",
                name = "Словакия"
            ),
            Country(
                code = "SI",
                name = "Словения"
            ),
            Country(
                code = "US",
                name = "Соединенные Штаты Америки"
            ),
            Country(
                code = "SB",
                name = "Соломоновы Острова"
            ),
            Country(
                code = "SO",
                name = "Сомали"
            ),
            Country(
                code = "SD",
                name = "Судан"
            ),
            Country(
                code = "SR",
                name = "Суринам"
            ),
            Country(
                code = "SL",
                name = "Сьерра-Леоне"
            ),
            Country(
                code = "TJ",
                name = "Таджикистан"
            ),
            Country(
                code = "TH",
                name = "Таиланд"
            ),
            Country(
                code = "TW",
                name = "Тайвань"
            ),
            Country(
                code = "TZ",
                name = "Танзания"
            ),
            Country(
                code = "TG",
                name = "Того"
            ),
            Country(
                code = "TT",
                name = "Тринидад и Тобаго"
            ),
            Country(
                code = "TN",
                name = "Тунис"
            ),
            Country(
                code = "TM",
                name = "Туркменистан"
            ),
            Country(
                code = "TR",
                name = "Турция"
            ),
            Country(
                code = "UG",
                name = "Уганда"
            ),
            Country(
                code = "UZ",
                name = "Узбекистан"
            ),
            Country(
                code = "UA",
                name = "Украина"
            ),
            Country(
                code = "UY",
                name = "Уругвай"
            ),
            Country(
                code = "FJ",
                name = "Фиджи"
            ),
            Country(
                code = "PH",
                name = "Филиппины"
            ),
            Country(
                code = "FI",
                name = "Финляндия"
            ),
            Country(
                code = "FR",
                name = "Франция"
            ),
            Country(
                code = "HR",
                name = "Хорватия"
            ),
            Country(
                code = "CF",
                name = "ЦАР"
            ),
            Country(
                code = "TD",
                name = "Чад"
            ),
            Country(
                code = "ME",
                name = "Черногория"
            ),
            Country(
                code = "CZ",
                name = "Чешская Республика"
            ),
            Country(
                code = "CL",
                name = "Чили"
            ),
            Country(
                code = "CH",
                name = "Швейцария"
            ),
            Country(
                code = "SE",
                name = "Швеция"
            ),
            Country(
                code = "LK",
                name = "Шри-Ланка"
            ),
            Country(
                code = "EC",
                name = "Эквадор"
            ),
            Country(
                code = "GQ",
                name = "Экваториальная Гвинея"
            ),
            Country(
                code = "ER",
                name = "Эритрея"
            ),
            Country(
                code = "EE",
                name = "Эстония"
            ),
            Country(
                code = "ET",
                name = "Эфиопия"
            ),
            Country(
                code = "ZA",
                name = "ЮАР"
            ),
            Country(
                code = "KR",
                name = "Южная Корея"
            ),
            Country(
                code = "SS",
                name = "Южный Судан"
            ),
            Country(
                code = "JM",
                name = "Ямайка"
            ),
            Country(
                code = "JP",
                name = "Япония"
            )

        )
    }
}
