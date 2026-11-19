import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        rateLimitTest: {
            executor: 'constant-arrival-rate',
            rate: 10,
            timeUnit: '1s',
            duration: '20s',
            preAllocatedVUs: 5,
            maxVUs: 20
        }
    },

    thresholds: {
        checks: ['rate==1'],
        dropped_iterations: ['count==0']
    }
};

export default function () {
    const response = http.get('http://localhost:8080/users');

    check(response, {
        'Status is 200 or 429': (r) => r.status === 200 || r.status === 429
    });
}