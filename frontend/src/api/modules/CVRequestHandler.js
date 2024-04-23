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
    }
}
