// 制作趋势图
let makeTrend = function (dataList, graphTitle, smallTitle, mainColour) {
  let lineChart = {
    chart: {
      zoomType: 'x',
    },
    title: {
      text: graphTitle,
    },
    subtitle: smallTitle === true ? {
      text: document.ontouchstart === undefined ?
      '鼠标拖动可以进行缩放' : '手势操作进行缩放'
    } : null,
    xAxis: {
      // 显示字符串
      type: 'category',
    },
    tooltip: {
      dateTimeLabelFormats: {
        millisecond: '%H:%M:%S.%L',
        second: '%H:%M:%S',
        minute: '%H:%M',
        hour: '%H:%M',
        day: '%Y-%m-%d',
        week: '%m-%d',
        month: '%Y-%m',
        year: '%Y'
      }
    },
    yAxis: {
      title: {
        text: '人数'
      }
    },
    legend: {
      enabled: false
    },
    plotOptions: {
      area: {
        fillColor: {
          linearGradient: {
            x1: 0,
            y1: 0,
            x2: 0,
            y2: 1
          },
          stops: [
            [0, mainColour],
            [1, mainColour + '00']
          ]
        },
        marker: {
          radius: 2
        },
        lineColor: mainColour, 
        lineWidth: 1.5,
        states: {
          hover: {
            lineWidth: 1
          }
        },
        threshold: null
      }
    },
    series: [{
      type: 'area',
      name: graphTitle,
      data: dataList
    }],
    // 去除水印
    credits: {
      enabled: false
    }
  };
  return lineChart;
}

export { makeTrend };