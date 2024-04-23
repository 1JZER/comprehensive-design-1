import http from '../axios'

export default {
    uploadFile(file) {
        const formData = new FormData();
        formData.append('file', file);
        return http({
                url: '/doIdentify',
                method: 'post',
                data: formData,
                responseType: 'blob'
            }
        )
    },
    // 查询历史记录集合
    listHistory(data) {
        return http({
            url: '/listHistory',
            method: 'get',
            params: data
        })
    },
    // 查询单个历史记录
    queryHistory(fileName) {
        return http({
            url: '/queryHistory',
            method: 'get',
            params: fileName,
            responseType: 'blob'
        })
    }

}
