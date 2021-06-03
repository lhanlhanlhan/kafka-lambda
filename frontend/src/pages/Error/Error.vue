<template>
  <div class="error-page">
    <b-container>
      <div class="errorContainer mx-auto">
        <h1 class="errorCode">404</h1>
        <p class="errorInfo">
          页面未找到；请查实后重试
        </p>
        <p class="errorHelp mb-3">
            ^_^
        </p>
        <b-button class="errorBtn" variant="success"
         href="#/app/index">
          回到主页
        </b-button>
      </div>
      <footer class="pageFooter">
        Han Li Studios
      </footer>
    </b-container>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage',
};
</script>

<style src="./Error.scss" lang="scss" scoped />
