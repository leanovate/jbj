<?php 

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// ----------------------
	// Sidebar Functions
	// ----------------------
	
	// Parent Class
	require_once('plugins/sidebar/Sidebar.php');

	$plugins = scandir(ROOT_DIR . '/scripts/plugins/sidebar/');

        foreach ($plugins as $plugin) {
		@include_once('plugins/sidebar/' . $plugin . '/plugin.php');
	}
	
?>
