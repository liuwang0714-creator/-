'use strict';
const uniID = require('uni-id-common')
const db = uniCloud.database();
exports.main = async (event = {}, context) => {
	try {
		// 尝试转为JSON对象
		event.body = JSON.parse(event.body)
	} catch (err) {
		// 如果转换失败，说明参数不是JSON字符串，直接返回错误信息给客户端
		return {
			errCode: 10000,
			errMsg: 'body参数格式错误，必须是JSON字符串格式'
		}
	}
	console.log('event.body: ', event.body)
	// 拿到 token 参数
	const { uni_id_token } = event.body
	// 检查 token 是否合法
	const payload = await uniID.createInstance({ context }).checkToken(uni_id_token)
	// 如果 token 不合法，返回错误信息给客户端
	if (payload.code) return payload
	// 如果 token 合法，返回用户信息给客户端
	const { data: [userInfo] } = await db.collection('uni-id-users').doc(payload.uid).field({ nickname: true, avatar_file: true, avatar: true }).get()
	if (!userInfo) {
		return {
			errCode: 10001,
			errMsg: '用户不存在'
		}
	}
	if (!userInfo.avatar_file) {
		userInfo.avatar_file = {
			url: userInfo.avatar
		}
	}
	let res = {
		errCode: 0,
		userInfo
	};
	console.log('res: ', res)
	return res
}