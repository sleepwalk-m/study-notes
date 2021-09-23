const puppeteer = require('puppeteer');

const http = require('http');
const url = require('url');

let data={
    content: '',
    param: ''
}


// 创建本地服务器来从其接收数据
const server = http.createServer(async (req, res) => {
    data['param'] = url.parse(req.url, true).query;
    res.writeHead(200, {'Content-Type': 'application/json'});
    data['content'] = await run();
    res.end(JSON.stringify({
        data: data
    }));
});

server.listen(8000);

async function run() {
    const browser = await puppeteer.launch({
        headless: false,
        ignoreHTTPSErrors: true,
        slowMo: 100,
        defaultViewport: {width: 1920, height: 1080}
    });

    try {
        const page = await browser.newPage();
        await page.goto("https://www.jd.com");
        await page.type('#key', '手机', {delay: 100}); // 输入变慢，像一个用户
        await page.click('.button');

        await page.waitFor(1000);

        autoScroll(page);// 翻页
        let aaa = await page.waitForSelector("ul.gl-warp");
        await sleep(2000);

        console.log(await page.content());
        return await page.content();

    } catch (e) {

    } finally {
        await browser.close();
    }
};


//延时函数
function sleep(delay) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            try {
                resolve(1)
            } catch (e) {
                reject(0)
            }
        }, delay)
    })
}


/**
 * 滚动
 * @param page
 * @returns {Promise<void>}
 */
async function autoScroll(page) {
    await page.evaluate(async () => {
        await new Promise((resolve, reject) => {
            var totalHeight = 0;
            var distance = 500;
            var timer = setInterval(() => {
                var scrollHeight = document.body.scrollHeight;
                window.scrollBy(0, distance);
                totalHeight += distance;
                if (totalHeight >= scrollHeight) {
                    clearInterval(timer);
                    resolve();
                }
            }, 100);
        });
    });
}


