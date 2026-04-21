package app.crimera.patches.instagram.misc.reels

import app.crimera.patches.instagram.misc.settings.settingsPatch
import app.crimera.patches.instagram.utils.Constants.COMPATIBILITY_INSTAGRAM
import app.crimera.patches.instagram.utils.Constants.PREF_CALL_DESCRIPTOR
import app.crimera.patches.instagram.utils.enableSettings
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import app.morphe.patcher.literal
import com.android.tools.smali.dexlib2.AccessFlags

object CommentButtonRenderFingerprint : Fingerprint(
    returnType = "L",
    parameters = listOf("L"),
    filters = listOf(
        string("android.widget.Button"),
        string("comment_button"),
        methodCall(
            smali = "Landroid/graphics/drawable/Drawable;->mutate()Landroid/graphics/drawable/Drawable;"
        ),
    )
)

object CommentCountFingerprint : Fingerprint(
    returnType = "L",
    parameters = listOf("L", "J", "J"),
    filters = listOf(
        string("ufi_count"),
        literal(0x7f0b0dac)
    )
)

@Suppress("unused")
val hideReelsCommentButton =
    bytecodePatch(
        name = "Hide Reels Comment Button",
        description = "Hide the comment button in Instagram Reels"
    ) {
        dependsOn(settingsPatch)
        compatibleWith(COMPATIBILITY_INSTAGRAM)

        execute {
            CommentButtonRenderFingerprint.method.addInstructions(
                0,
                """
                    $PREF_CALL_DESCRIPTOR->showReelsCommentButton()Z
                    move-result v0
                    if-nez v0, :piko
                    return-object v0
                    :piko
                    nop
                """.trimIndent(),
            )
            CommentCountFingerprint.method.addInstructions(
                0,
                """
                    $PREF_CALL_DESCRIPTOR->showReelsCommentButton()Z
                    move-result v0
                    if-nez v0, :piko
                    return-object v0
                    :piko
                    nop
                """.trimIndent(),
            )
            enableSettings("hideReelsCommentButton")
        }
    }
