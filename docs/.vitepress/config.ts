import Unocss from 'unocss/vite'
import {defineConfig} from 'vitepress'
import fs from 'fs'

export default defineConfig({
    lang: 'en-US',
    title: 'MongoCamp MongoDB Driver',
    description: 'MongoCamp MongoDB Driver fka. simple-mongo. The easy way to connect scala to MongoDB.',

    lastUpdated: true,

    themeConfig: {
        logo: '/logo_without_text.png',

        nav: nav(),
        search: {
            provider: 'local'
        },

        sidebar: {
            '/documentation/': sidebarDocumentation()
            // '/config/': sidebarConfig(),
            // '/plugins/': sidebarPlugins()
        },

        editLink: {
            pattern: 'https://github.com/MongoCamp/mongodb-driver/edit/master/docs/:path',
            text: 'Edit this page on GitHub'
        },

        socialLinks: [
            {icon: 'github', link: 'https://github.com/MongoCamp/mongodb-driver'}
        ],

        footer: {
            message: 'Released under the Apache License 2.0.',
            copyright: 'Copyright © 2023 - MongoCamp Team'
        },

    },
    vite: {
        plugins: [
            Unocss({
                configFile: '../../unocss.config.ts',
            }),
        ],
    },

})

function nav() {
    var versionInfos = JSON.parse(fs.readFileSync('docs/versions.json', 'utf-8'))

    return [
        {
            text: 'Documentation',
            items: [
                {text: 'Getting Started', link: '/documentation/getting-started'},
                {text: 'Database', link: '/documentation/database/'},
                {text: 'MongoDAO', link: '/documentation/mongo-dao/'},
                {text: 'GridFsDAO', link: '/documentation/gridfs-dao/'},
                {text: 'Collection', link: '/documentation/collection/'},
                {text: 'LocalServer', link: '/documentation/local-server'},
                {text: 'SQL', link: '/documentation/sql'}
            ]
        },
        {
            text: versionInfos.mongocamp,
            items: [
                {
                    text: 'Changelog',
                    link: '/changelog.html'
                },
            ],
        },

    ]
}

function sidebarDocumentation() {
    return [
        {
            text: 'Getting Started',
            link: '/documentation/getting-started'
        },
        {
            text: 'Database',
            link: '/documentation/database/',
            collapsible: true,
            collapsed: true,
            items: [
                {text: 'Introduction', link: '/documentation/database/'},
                {text: 'Mongo Config', link: 'documentation/database/config'},
                {text: 'DatabaseProvider', link: 'documentation/database/provider'},
                {text: 'Reactive Streams', link: 'documentation/database/reactive-streams'},
                {text: 'Bson', link: 'documentation/database/bson'},
                {text: 'Relationships', link: 'documentation/database/relationships'},
                {text: 'Lucene Query', link: '/documentation/database/lucene'}
            ]
        },
        {
            text: 'MongoDAO',
            link: '/documentation/mongo-dao/',
            collapsible: true,
            collapsed: true,
            items: [
                {text: 'Introduction', link: '/documentation/mongo-dao/'},
                {text: 'MongoDAO Base', link: '/documentation/mongo-dao/base'},
                {text: 'CRUD Functions', link: '/documentation/mongo-dao/crud'},
                {text: 'Search Functions', link: '/documentation/mongo-dao/search'}
            ]
        },
        {
            text: 'GridFsDAO',
            link: '/documentation/gridfs-dao/',
            collapsible: true,
            collapsed: true,
            items: [
                {text: 'Introduction', link: '/documentation/gridfs-dao/'},
                {text: 'CRUD Functions', link: '/documentation/gridfs-dao/crud'},
                {text: 'Metadata', link: '/documentation/gridfs-dao/metadata'}
            ]
        },
        {
            text: 'Collection',
            link: '/documentation/collection/',
            collapsible: true,
            collapsed: true,
            items: [
                {text: 'Introduction', link: '/documentation/collection/'},
                {text: 'Aggregation', link: '/documentation/collection/aggregation'},
                {text: 'Pagination', link: '/documentation/collection/pagination'},
                {text: 'Schema analyse', link: '/documentation/collection/analyse-schema'},
            ]
        },
        {
            text: 'SQL', items: [
                {text: 'Query Holder', link: '/documentation/sql/queryholder'},
                {text: 'JDBC driver', link: '/documentation/sql/jdbc-driver'},
            ],
            collapsible: true,
            collapsed: true,
        },
        {
            text: 'Known Issues',
            link: '/documentation/known-issues', items: [
                {text: 'Json Converter', link: '/documentation/known-issues/json-converter'},
            ],
            collapsible: true,
            collapsed: true,
        },
        {
            text: 'LocalServer',
            link: '/documentation/local-server'
        },
    ]
}
