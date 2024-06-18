import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'
const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus)
app.use(router)
app.component('MdPreview', MdPreview)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.mount('#app')
