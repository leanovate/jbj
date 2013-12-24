<?php
	/**
	* Preferences menu widget.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Preferences extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Preferences () {
			$this->plugin = 'Preferences';
			$this->loadPrefs();
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb( 'menu_setup' );
		}
		
		function getContent () {
			global $blog_config;
			$str = '';
	
			if ( $GLOBALS[ 'logged_in' ] == true ) {
				$str = '';
				$str  .= '<a href="' . BASEURL . 'categories.php">' . _sb('menu_categories') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'add_block.php">' . _sb('menu_add_block') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'setup.php">' . _sb('menu_setup') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'plugins.php">' . _sb('menu_plugins') . '</a><br />';
				if ($blog_config->getTag('USE_EMOTICONS')) {
					$str  .= '<a href="' . BASEURL . 'emoticons.php">' . _sb('menu_emoticons') . '</a><br />';
				}
				$str  .= '<a href="' . BASEURL . 'themes.php">' . _sb('menu_themes') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'colors.php">' . _sb('menu_colors') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'options.php">' . _sb('menu_options') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'info.php">' . _sb('menu_info') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'manage_users.php">' . _sb('manage_users') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'phpinfo.php">' . _sb('manage_php_config') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'zip.php?dirs=content,config">' . _sb('zip_backup') . '</a><br />';
				$str  .= '<a href="' . BASEURL . 'zip.php?dirs=images">' . _sb('image_backup') . '</a><br />';
				$str  .= '<hr />';
				$str  .= '<a href="' . BASEURL . 'moderation.php">' . _sb('menu_moderation') . '</a><br />';
				if ( $blog_config->getTag( 'blog_comments_moderation' ) ) {
					$str  .= '<a href="' . BASEURL . 'comments_moderation.php">' . _('menu_commentmoderation') . ' (' . get_unmodded_count(1) . ')</a><br />';
				}
			}
			
			return $str;
		}
	}
?>
