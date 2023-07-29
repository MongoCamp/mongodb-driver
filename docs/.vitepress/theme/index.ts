import DefaultTheme from 'vitepress/theme'
import './custom.css'
import type {App} from 'vue'
import 'uno.css'
import DependecyGroup from './components/DependecyGroup.vue'

export default {
    ...DefaultTheme,
    enhanceApp({app}: { app: App }) {
        app.component('DependecyGroup', DependecyGroup)
    }
}

