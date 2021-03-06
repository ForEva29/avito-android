package com.avito.plugin

import com.avito.kotlin.dsl.typedNamed
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

internal const val markReportAsSourceTaskName = "markReportAsSourceForTMSTask"

fun TaskContainer.markReportAsSourceTask(): TaskProvider<MarkReportAsSourceTask> =
    typedNamed(markReportAsSourceTaskName)

const val tmsPluginId = "com.avito.android.tms"
