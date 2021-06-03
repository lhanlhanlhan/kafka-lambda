<template>
  <div v-if="!excluded">
    <b-breadcrumb :items="tree"></b-breadcrumb>
  </div>
</template>
<script>
  export default {
    name: 'BreadcrumbHistory',
    props: {
      exclude: {type: Array, default: () => []}
    },
    computed: {
      excluded() {
        return this.exclude.indexOf(this.$route.path.split('/').pop()) > -1;
      },
      tree() {
        let pageName = {
          'app': '功能',
          'index': '主页',
          'trends': '趋势分析',
          'conf-trends': '确诊趋势分析',
          'demise-trends': '病亡趋势分析',
          'cured-trends': '治愈趋势分析',
          'weibocount-trends': '热度趋势',
          'device-worcloud':'发布设备',
          'wordclouds':'词云图',
          'who':'人群画像',
          'weibosentiment-trends':'情感变化分析',
          'maps': '疫情地图',
          'dom-maps': '全国疫情地图',
          'prov-maps': '各地疫情地图',
          'class': '数据分类',
          'algos': '算法'
        };
        return ['阁下在']
          .concat(this.$route.path
            .split('/')
            .slice(1)
            .map(function(w) {
                let pName = pageName[w];
                if (pName) {
                  return pName;
                } else return w
              }
            )
          );
      }
    }
  }
</script>
