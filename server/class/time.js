/* eslint-disable no-undef */

class dateTool {
    constructor() {
        this.date = {};
        this.hello = [
            "凌晨",
            "早上",
            "上午",
            "中午",
            "下午",
            "傍晚",
            "晚上"
        ];
        this.helloText = "凌晨";
    }
    
    Date() {
        this.date.Year = new Date().getFullYear();
        this.date.Month = new Date().getMonth() + 1;
        this.date.Date = new Date().getDate();
        this.date.Hou =
            new Date().getHours().toString().length < 2
                ? "0" + new Date().getHours().toString()
                : new Date().getHours().toString();
        this.date.Min =
            new Date().getMinutes().toString().length < 2
                ? "0" + new Date().getMinutes().toString()
                : new Date().getMinutes().toString();
        this.date.Sen =
            new Date().getSeconds().toString().length < 2
                ? "0" + new Date().getSeconds().toString()
                : new Date().getSeconds().toString();
        this.date.Day = new Date(`${this.date.Year}-${this.date.Mouth}-${this.date.Date}`).getDay();
        this.date.Week = [
            "星期日",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六"
        ][this.date.Day];
        return this.date
    }

    Hello() {
        let hour = new Date().getHours();
        if (hour >= 0 && hour < 6) {
            return this.hello[0];
        } else if (hour >= 6 && hour < 8) {
            return this.hello[1];
        } else if (hour >= 8 && hour < 12) {
            return this.hello[2];
        } else if (hour === 12) {
            return this.hello[3];
        } else if (hour >= 13 && hour < 18) {
            return this.hello[4];
        } else if (hour.Hours === 18) {
            return this.hello[5];
        } else if (hour >= 18 && hour < 24) {
            return this.hello[6];
        }
    }
}

module.exports = dateTool;