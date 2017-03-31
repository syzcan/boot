/*!
 * zutil工具类，包含表单序列化操作
 * author: zong
 * version: 1.0
 * build: 2017-03-11
 */
var zutil = function() {
	/**
	 * 序列化form表单数据为json对象 Object {name: "zong", age: "27"}
	 * 
	 * @param $form
	 * @returns json对象
	 */
	this.formJson = function($form) {
		var array = $form.serializeArray();
		var json = {};
		$.each(array, function(i, field) {
			if (json[field.name]) {
				if ($.isArray(json[field.name])) {
					json[field.name].push(field.value);
				} else {
					json[field.name] = [ json[field.name], field.value ];
				}
			} else {
				json[field.name] = field.value;
			}
		});
		return json;
	}

	this.formJsonString = function($form) {
		return JSON.stringify(formJson($form));
	}

	/**
	 * 序列化form表单数据为字符窜 name=zong&age=27
	 * 
	 * @param $form
	 */
	this.formData = function($form) {
		return $form.serialize();
	}

	/**
	 * restful方式提交form数据，通过request payload传递json字符串
	 * 
	 * @param $form
	 * @param type
	 * @param 回调函数func
	 */
	this.restful = function($form, type, func) {
		var url = $form.attr('action');
		var contentType = 'application/json';
		var data = formJsonString($form);
		if (type == undefined) {
			type = 'POST';
		}
		$.ajax({
			url : url,
			contentType : contentType,
			type : type,
			dataType : 'json',
			data : data,
			success : function(data) {
				if (data.errMsg == 'success') {
					if (typeof func === "function") {
						func(data);
					}
					layer.msg('操作成功');
				} else {
					layer.msg(data.errMsg);
				}
				// layer.closeAll();
			},
			error : function() {
				layer.msg('请求错误');
			}
		});
	}
	return this;
}()