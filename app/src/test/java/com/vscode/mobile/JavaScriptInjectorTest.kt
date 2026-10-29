package com.vscode.mobile

import com.vscode.mobile.model.CodespacePanel
import com.vscode.mobile.model.ViewMode
import com.vscode.mobile.webview.JavaScriptInjector
import org.junit.Assert.assertTrue
import org.junit.Test

class JavaScriptInjectorTest {

    @Test
    fun `mobile bootstrap contains MOBILE mode marker`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.MOBILE)
        assertTrue("Script should reference MOBILE mode", script.contains("MOBILE"))
    }

    @Test
    fun `desktop bootstrap contains DESKTOP mode marker`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.DESKTOP)
        assertTrue("Script should reference DESKTOP mode", script.contains("DESKTOP"))
    }

    @Test
    fun `mobile bootstrap hides activity bar in mobile mode`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.MOBILE)
        assertTrue("Mobile mode should hide the activity bar", script.contains(".activitybar"))
    }

    @Test
    fun `desktop bootstrap does not apply mobile overrides initially`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.DESKTOP)
        assertTrue("Desktop mode should inject an empty stylesheet initially",
            script.contains("MODE === 'DESKTOP' ? '' : MOBILE_CSS"))
    }

    @Test
    fun `desktop bootstrap retains mobile CSS for switching back`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.DESKTOP)
        assertTrue("Desktop mode must retain mobile rules", script.contains("left: -9999px"))
        assertTrue("Mobile rules must be saved on the stylesheet",
            script.contains("s.setAttribute('data-mobile-css', MOBILE_CSS)"))
        assertTrue("View toggle must restore the saved stylesheet",
            script.contains("s.getAttribute('data-mobile-css')"))
    }

    @Test
    fun `view mode diagnostics report the current mode`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.MOBILE)
        assertTrue(script.contains("currentMode = mode"))
        assertTrue(script.contains("mode: currentMode"))
    }

    @Test
    fun `bootstrap defines VSCMobile global object`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.MOBILE)
        assertTrue(script.contains("window.VSCMobile"))
    }

    @Test
    fun `bootstrap sets double-injection guard`() {
        val script = JavaScriptInjector.buildMobileBootstrap(ViewMode.MOBILE)
        assertTrue(script.contains("__vscMobileInjected"))
    }

    @Test
    fun `activate panel script contains panel id`() {
        val script = JavaScriptInjector.buildActivatePanelScript(CodespacePanel.TERMINAL)
        assertTrue(script.contains(CodespacePanel.TERMINAL.activityBarId))
    }

    @Test
    fun `activate panel script is self-contained`() {
        // The script should work without window.VSCMobile being set up
        val script = JavaScriptInjector.buildActivatePanelScript(CodespacePanel.EXPLORER)
        assertTrue("Script should contain selector strategy", script.contains("querySelector"))
        assertTrue("Script should contain panel label", script.contains(CodespacePanel.EXPLORER.displayLabel))
    }

    @Test
    fun `activate panel script contains keyboard shortcut fallback`() {
        val script = JavaScriptInjector.buildActivatePanelScript(CodespacePanel.EXPLORER)
        assertTrue("Script should contain keyboard shortcut dispatch", script.contains("KeyboardEvent"))
    }

    @Test
    fun `set view mode script for mobile`() {
        val script = JavaScriptInjector.buildSetViewModeScript(ViewMode.MOBILE)
        assertTrue(script.contains("MOBILE"))
        assertTrue(script.contains("setViewMode"))
    }

    @Test
    fun `set view mode script for desktop`() {
        val script = JavaScriptInjector.buildSetViewModeScript(ViewMode.DESKTOP)
        assertTrue(script.contains("DESKTOP"))
    }
}
