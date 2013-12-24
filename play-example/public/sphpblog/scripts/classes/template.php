<?php
	// TODO remove dependency on config.ph for TEMPLATE_DIR
	/**
	* Template class.
	*
	* @author		Alexander Palmo <apalmo at bigevilbrain dot com>
	* @access		public static
	*/
	
	// Template Class
	class Template {
		var $path, $template, $tags, $keys;
		
		// Constructor
		function Template($path=null) {
		
			// Variables
			$this->path = TEMPLATE_DIR.'default.tpl';
			$this->template = '';
			$this->tags = array();
			$this->keys = array();
		
			// Set path
			if (isset($path)) {
				$this->path = $path;
			}
			
			// Load template
			$contents = fileio::read_file($this->path, true);
			if ($contents!==false) {
				$this->template = $contents;
			}
			
			// Find tags
			preg_match_all('/\{[A-Z1-9_]+?\}/', $this->template, $matches);
			
			$this->tags = $matches[0];
			
			// Create replacement array
			$arr = array();
			for ($i=0; $i<count($this->tags); $i++) {
				$key = $this->tags[$i];
				$arr[$key] = '';
			}
			$this->keys = $arr;
			
		}
		
		function getTags() {
			return($this->tags);
		}
		
		function getTag($tag) {
			return $this->keys[$tag];
		}
		
		function setTag($tag, $value) {
			$this->keys[$tag] = $value;
		}
		
		function appendTag($tag, $value) {
			$this->keys[$tag] = $this->keys[$tag] . $value;
		}
		
		function prependTag($tag, $value) {
			$this->keys[$tag] = $value . $this->keys[$tag];
		}
		
		function getHTML() {
			$search = array_keys($this->keys);
			$replace = array_values($this->keys);
			
			$str = str_replace($search,$replace,$this->template);
			
			return($str);
		}
	}
?>
