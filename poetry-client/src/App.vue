<template>
  <div id="app-root">
    <NavBar />
    <main class="app-container">
      <router-view v-slot="{ Component, route }">
        <Transition :name="route.meta.transition || 'page'" mode="out-in">
          <component :is="Component" :key="route.path" />
        </Transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
#app-root { min-height: 100vh; position: relative; }
.app-container {
  max-width: 1200px; margin: 0 auto; padding: 20px; position: relative; z-index: 1;
}
</style>
