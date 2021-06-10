<template>
  <div>
    <h1 class="page-title">🌟 厦大商城 6·18 大屏 🌟</h1>
    <b-row>
      <!-- 实时总交易量 -->
      <b-col xs="12" lg="8">
        <b-row>
          <b-col class="col-12">
            <Widget collapse>
              <h4>实时交易量</h4>
              <p>Total Trade Amount</p>
              <div
                class="widget-padding-md w-100 h-100 text-center border rounded"
              >
                <p>全国总销售额 (自 2021-6-6 10:00)</p>
                <span class="display-2 text-warning" v-if="ready">
                  <strong>$ {{ totalAmount / 100.0 }}</strong>
                </span>
                <span class="display-2 text-warning" v-else>
                  <strong>载入中</strong>
                </span>
                <p>全国订单笔数</p>
                <span class="display-3" v-if="ready">
                  {{ totalTrade }}
                </span>
                <span class="display-3" v-else>
                  请稍候
                </span>
              </div>
            </Widget>
          </b-col>
        </b-row>
        <!-- 实时城市交易量、交易金额图 -->
        <b-row>
          <b-col class="col-12">
            <Widget collapse>
              <h4>实时订单来源概况</h4>
              <p>City Order Details</p>
              <b-row>
                <b-col class="col-6">
                  <highcharts :options="pieCityTrade" />
                </b-col>
                <b-col class="col-6">
                  <highcharts :options="pieCityAmount" />
                </b-col>
              </b-row>
            </Widget>
          </b-col>
        </b-row>
      </b-col>

      <!-- 土豪城市榜单 -->
      <b-col xs="12" lg="4">
        <Widget collapse>
          <h4>「土豪城市」榜</h4>
          <p>Tuhao City List</p>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>城市</th>
                <th>当前销售额</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(city, cityIdx) in sortedCityAmount" :key="cityIdx">
                <td v-if="cityIdx == 0">
                  <span class="fw-bold">
                    {{ city.city }}
                  </span>
                  &nbsp;
                  <b-badge variant="danger" class="text-white">#1</b-badge>
                </td>
                <td v-else-if="cityIdx == 1">
                  <span class="fw-bold">
                    {{ city.city }}
                  </span>
                  &nbsp;
                  <b-badge variant="warning" class="text-white">#2</b-badge>
                </td>
                <td v-else-if="cityIdx == 2">
                  <span class="fw-bold">
                    {{ city.city }}
                  </span>
                  &nbsp;
                  <b-badge variant="success" class="text-white">#3</b-badge>
                </td>
                <td v-else>
                  {{ city.city }}
                </td>
                <td>$ {{ city.amount / 100.0 }}</td>
              </tr>
            </tbody>
          </table>
        </Widget>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import Widget from "@/components/Widget/Widget";
// SockJS
import SockJS from "sockjs-client";
import Stomp from "stompjs";
// Pie
import { Chart } from "highcharts-vue";
import { pieChart } from "../pie";

export default {
  name: "HomePage",
  components: { Widget, highcharts: Chart },
  data: function () {
    return {
      wsClient: null,
      pieCityTrade: pieChart([["南宁", 2283]], "城市销量"),
      pieCityAmount: pieChart([["南宁", 1211231]], "城市销售额"),
      sortedCityAmount: [{ city: "南宁", amount: 1211231 }],
      totalAmount: 1211231,
      totalTrade: 2283,

      ready: false,
    };
  },
  methods: {
    makePieCityTrade(data) {
      let tradeArr = [];
      let sumTrade = 0;
      data.forEach((city) => {
        tradeArr.push([city.city, city.number]);
        sumTrade += city.number;
      });
      this.pieCityTrade = pieChart(tradeArr, "城市销量");
      this.totalTrade = sumTrade;
    },
    makePieCityAmount(data) {
      let amountArr = [];
      let sumAmount = 0;
      data.forEach((city) => {
        amountArr.push([city.city, city.amount]);
        sumAmount += city.amount;
      });
      this.pieCityAmount = pieChart(amountArr, "城市销售额");
      this.totalAmount = sumAmount;
    },
    makeTuHaoCityList(data) {
      let sortedData = data.sort((a, b) => {
        return b.amount - a.amount;
      });
      this.sortedCityAmount = sortedData;
    },
  },
  created: function () {
    let socket = new SockJS("https://board.ac.han-li.cn/api/stomp");
    this.wsClient = Stomp.over(socket);
    // 连接 ws
    let that = this;
    this.wsClient.connect(
      {},
      () => {
        // 订阅 ws 话题
        that.wsClient.subscribe("/topic/the-board", msg => {
          let msgBody = JSON.parse(msg.body);
          let cityTotalAmount = msgBody.cityTotalAmount;
          let cityTotalTrade = msgBody.cityTotalTrade;
          // 制作图表
          that.makePieCityAmount(cityTotalAmount);
          that.makePieCityTrade(cityTotalTrade);
          that.makeTuHaoCityList(cityTotalAmount);
          that.ready = true;
        });
      },
      (err) => {
        console.log("连接失败！", err);
      }
    );
  },
};
</script>
