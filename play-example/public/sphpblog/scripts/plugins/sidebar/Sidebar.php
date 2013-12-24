<?php
	/**
	* Widget is the base class for all the sidebar plugin classes.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Sidebar {
		var $prefs = array();
		
		/* ------ INITIALIZE ------ */
		
		function Sidebar () {
			// nothing
		}
		
		/* ------ PREFERENCES ------ */
		
		// Return default values for plugin.
		function defaultPrefs () {
			$arr = array(
				'plugin' => $this->plugin,
				'enabled' => true
			);
			return $arr;
		}
		
		function loadPrefs () {
			$str = fileio::read_file( CONFIG_DIR . 'plugins/sidebar/' . $this->plugin . '.txt' );
			
			if ( $str == NULL) {
				// no file
				$this->prefs = $this->defaultPrefs(); // get default prefs
				$result = $this->savePrefs();
				
			} else {
				$arr = $this->kexplode($str);
				$arr = array_merge($this->defaultPrefs(),$arr);
				$this->prefs =$arr;
				
			}
		}
		
		function savePrefs () {
			fileio::make_dir( CONFIG_DIR . 'plugins/sidebar' );
			$str = $this->kimplode($this->prefs);
			$result = fileio::write_file( CONFIG_DIR . 'plugins/sidebar/' . $this->plugin . '.txt', $str );
			return $result;
		}
		
		function setPref($key, $value) {
			$this->prefs[$key] = $value;
		}
		
		function getPref($key) {
			return $this->prefs[$key];
		}
		
		/* ------ DISPLAY ------ */
		
		// Return associative array for display by theme.php
		function display () {
			if ($this->getEnabled()) {
				$arr = array(
					'title' => $this->getTitle(),
					'content' => $this->getContent()
				);
				return $arr;
			}
		}
		
		/* ------ OPTIONS ------ */
		
		/*
		function optionsForm () {
			return;
		}
		
		function optionsPost () {
			return;
		}
		*/
		
		/* ------ GETTERS & SETTERS ------ */
		
		// Return enabled status.
		function getEnabled () {
			return $this->prefs['enabled'];
		}
		
		function setEnabled ( $flag=true ) {
			$this->prefs['enabled'] = $flag;
			$this->savePrefs();
		}
		
		// Return if the widget has options.
		function getOptions () {
			$methods = array_map(strtolower, get_class_methods($this));
			if (!$methods) {
				$methods = array();
			}
			return in_array( strtolower("optionsForm"), $methods );
		}
		
		// Return plugin name.
		function getPluginID () {
			return $this->prefs['plugin'];
		}
		
		function getTitle () {
			$str = $this->getPluginName();
			return $str;
		}
		
		function getContent () {
			$str = '';
			return $str;
		}
		
		/* ------ UTILITY ------ */
		
		// Implode an associative array
		function kimplode( $arr, $delim='|' ) {
			$str = '';
			$temp_arr = array();
			foreach ($arr as $key => $val) {
				array_push($temp_arr,str_replace( '|', '&#124;', $key ));
				array_push($temp_arr,str_replace( '|', '&#124;', $val ));
			}
			$str = implode($delim,$temp_arr);
			return $str;
		}
		
		// Explode a string to an associative array
		function kexplode( $str, $delim='|' ) {
			$arr = explode( $delim, $str );
			
			$result = array();
			for ( $i = 0; $i < count( $arr ); $i = $i + 2 ) {
				$key = str_replace( '&#124;', '|', $arr[ $i ] );
				$result[ $key ] = str_replace( '&#124;', '|', $arr[ $i+1 ] );
			}
			
			return $result;
		}
	
	}
?>