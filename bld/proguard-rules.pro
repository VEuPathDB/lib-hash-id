-verbose

-optimizationpasses 5

-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class):<java.home>/jmods/java.logging.jmod(!**.jar;!module-info.class)

-keep class org.veupathdb.lib.hash_id.HashID

-keepattributes RuntimeVisibleAnnotations