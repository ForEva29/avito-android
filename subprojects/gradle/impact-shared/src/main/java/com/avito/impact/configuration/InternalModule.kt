package com.avito.impact.configuration

import com.avito.impact.ConfigurationType
import com.avito.impact.changes.ChangesDetector
import com.avito.impact.fallback.ImpactFallbackDetector
import com.avito.impact.util.Equality
import com.avito.kotlin.dsl.ProjectProperty
import org.gradle.api.Project

/**
 * injection to any module in project to carry dependencies info relevant to in-house plugins, such as:
 * Impact analysis / Code ownership etc.
 */
class InternalModule(
    val project: Project,
    val changesDetector: ChangesDetector,
    val fallbackDetector: ImpactFallbackDetector
) : Equality by InternalModuleEquality(project) {

    val path: String = project.path

    val implementationConfiguration = ImplementationConfiguration(this)
    val testConfiguration = TestConfiguration(this)
    val androidTestConfiguration = AndroidTestConfiguration(this)
    val lintConfiguration = LintConfiguration(this)
    val configurations = listOf(
        implementationConfiguration,
        testConfiguration,
        androidTestConfiguration,
        lintConfiguration
    )

    /**
     * Module has been changed itself or transitively via project dependencies
     */
    fun isModified(configurationType: ConfigurationType): Boolean = getConfiguration(configurationType).isModified

    fun isModified(): Boolean = configurations.any { it.isModified }

    fun getConfiguration(configurationType: ConfigurationType): SimpleConfiguration = when (configurationType) {
        ConfigurationType.IMPLEMENTATION -> implementationConfiguration
        ConfigurationType.UNIT_TESTS -> testConfiguration
        ConfigurationType.ANDROID_TESTS -> androidTestConfiguration
        ConfigurationType.LINT -> lintConfiguration
    }

    override fun toString(): String = "InternalModule[${project.path}]"
}

var Project.internalModule: InternalModule by ProjectProperty.lateinit()

/**
 * Equality delegated to project
 */
internal class InternalModuleEquality(private val project: Project) : Equality {

    override fun equals(other: Any?): Boolean {
        if (InternalModule::class.java != other?.javaClass) return false
        other as InternalModule
        if (project != other.project) return false
        return true
    }

    override fun hashCode(): Int = project.hashCode()
}
