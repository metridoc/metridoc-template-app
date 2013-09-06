import org.apache.commons.lang.SystemUtils
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import org.slf4j.LoggerFactory

def rootLoader = rootLoader = GrailsWebApplicationContext.class.classLoader

def driverDirectory = new File("${SystemUtils.USER_HOME}/.grails/drivers")
log.info "searching $driverDirectory for drivers"

def existsAndIsDirectory = driverDirectory.exists() && driverDirectory.isDirectory()

if (existsAndIsDirectory) {
    log.info "$driverDirectory exists, checking for drivers"
    driverDirectory.listFiles().each {
        if (it.name.endsWith(".jar")) {
            def url = it.toURI().toURL()
            LoggerFactory.getLogger("config.Config").info "adding driver ${url}" as String
            rootLoader.addURL(url)
        }
        else {
            log.info "$it is not a driver"
        }
    }
}
else {
    log.info "$driverDirectory does not exists or is not a directory"
}

grails.converters.default.pretty.print = true
metridoc.home = "${userHome}/.metridoc"
grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data',
        xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        xls: "application/vnd.ms-excel"
]


grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.views.gsp.sitemesh.preprocess = true
grails.scaffolding.templates.domainSuffix = 'Instance'
grails.json.legacy.builder = false
grails.enable.native2ascii = true
grails.spring.bean.packages = []
grails.web.disable.multipart = false
grails.exceptionresolver.params.exclude = ['password']
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.gsp.reload.enable = true
        grails.resources.processing.enabled = true
        grails.resources.debug = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

grails.config.locations = []

if (new File("${metridoc.home}/MetridocConfig.groovy").exists()) {
    log.info "found MetridocConfig.groovy, will add to configuration"
}
grails.config.locations << "classpath:MetridocConfig.groovy"
grails.config.locations << "file:${metridoc.home}/MetridocConfig.groovy"


if (System.properties["${appName}.config.location"]) {
    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
}

log4j = {

    appenders {
        def logLocation = "${SystemUtils.USER_HOME}/.metridoc/logs/metridoc"
        def baseLog = "${logLocation}.log"
        println "base log stored at $baseLog"
        rollingFile name: "file",
                maxBackupIndex: 10,
                maxFileSize: "1MB",
                file: baseLog

        def stacktraceLog = "${logLocation}-stacktrace.log"
        println "stacktrace log stored at $stacktraceLog"
        rollingFile name: "stacktrace",
                maxFileSize: "1MB",
                maxBackupIndex: 10,
                file: stacktraceLog
    }


    error 'org.codehaus.groovy',
            'grails.app.resourceMappers',
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate',
            'metridoc.camel',
            'grails.plugin',
            'org.grails',
            'org.quartz',
            'ShiroGrailsPlugin',
            'grails.util',
            'org.grails.plugin.resource.BundleResourceMapper',
            'org.apache',
            'net.sf' //ehcache

    root {
        info 'stdout', 'file'
    }
}