<?php
	/**
	* Archive tree view.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Archives extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Archives () {
			$this->plugin = 'Archives';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_archive');
		}
		
		function getContent () {
			$str = '';
			$str .= '<a href="archives.php">' . _sb('menu_viewarchives') . '</a><br />' . "\n";
			$str .= read_menus_tree( $GLOBALS['month'], $GLOBALS['year'], $GLOBALS['day'], 0 );
			
			return $str;
		}
	}
?>
