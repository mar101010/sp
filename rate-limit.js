import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 5,
    iterations: 20,
    thresholds: {
        checks: ['rate==1'],
        http_req_failed: ['rate==0.75']
    }
};

/*export const options = {
    stages: [
        { duration: '10s', target: 5 },
        { duration: '20s', target: 5 },
        { duration: '10s', target: 10 },
        { duration: '10s', target: 0 }
    ]
}; */

export default function () {
    const response = http.get('http://localhost:8080/users');

    check(response, {
        'Status is 200 or 429': (r) => r.status === 200 || r.status === 429
    });
}


/*export default function () {
    const response = http.get('http://localhost:8080/users');

    console.log(`Request: ${__ITER + 1}, Status: ${response.status}`);
}*/