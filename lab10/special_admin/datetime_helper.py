from datetime import datetime


def simple_representation(_datetime):
    # YEAR
    year = _datetime.year
    year_str = str(year)
    if year < 10:
        year_str = "0" + str(year)
    # MONTH
    month = _datetime.month
    month_str = str(month)
    if month < 10:
        month_str = "0" + str(month)
    # DAY
    day = _datetime.day
    day_str = str(day)
    if day < 10:
        day_str = "0" + str(day)
    # HOUR
    hour = _datetime.hour
    hour_str = str(hour)
    if hour < 10:
        hour_str = "0" + str(hour)
    # MINUTE
    minute = _datetime.minute
    minute_str = str(minute)
    if minute < 10:
        minute_str = "0" + str(minute)
    # SECOND
    second = _datetime.second
    second_str = str(second)
    if second < 10:
        second_str = "0" + str(second)

    return day_str + "/" + month_str + "/" + year_str + " " + "09" + ":" + "30" + ":" + "00"


def get_datetime(datetime_string):
    date_time_parts = datetime_string.split(" ")
    date = date_time_parts[0].split("/")
    time = date_time_parts[1].split(":")
    ret = datetime.strptime(date[0] + " " + date[1] + " " + date[2] + " " + time[0] + " " + time[1] + " " + "00",
                            "%d %m %Y %H %M %S")
    return ret
