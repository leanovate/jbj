<?php
	/**
	* Authoring menu widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class AuthoringMenu extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function AuthoringMenu () {
			$this->plugin = 'AuthoringMenu';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_menu');
		}
		
		function getContent () {
			$str = '';
	
			if ( $GLOBALS[ 'logged_in' ] == true ) {
				$str .= '<a href="' . BASEURL . 'add.php">' . _sb( 'menu_add' ) . '</a><br />';
				$str .= '<a href="' . BASEURL . 'add_static.php">' . _sb( 'menu_add_static' ) . '</a><br />';
				if (ini_get('file_uploads'))
					$str .= '<a href="' . BASEURL . 'upload_img.php">' . _sb( 'menu_upload' ) . '</a>';
			}
			
			return $str;
		}
	}
?>
