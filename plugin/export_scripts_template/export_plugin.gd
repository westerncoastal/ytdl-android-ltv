@tool
extends EditorPlugin

var export_plugin : AndroidExportPlugin

func _enter_tree():
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)

func _exit_tree():
	remove_export_plugin(export_plugin)
	export_plugin = null

class AndroidExportPlugin extends EditorExportPlugin:
	var _plugin_name = "FlowerPotter"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		# This dynamically finds the directory this script is in
		var base_dir = get_script().get_path().get_base_dir()
		if debug:
			return PackedStringArray([base_dir + "/bin/debug/" + _plugin_name + "-debug.aar"])
		else:
			return PackedStringArray([base_dir + "/bin/release/" + _plugin_name + "-release.aar"])

	func _get_android_dependencies(platform, debug):
		if not _supports_platform(platform):
			return PackedStringArray()
		return PackedStringArray([
			"io.github.junkfood02.youtubedl-android:library:0.18.1",
			"io.github.junkfood02.youtubedl-android:ffmpeg:0.18.1"
		])

	func _get_name():
		return _plugin_name
