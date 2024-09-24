import {instance} from '@/api/axios';
import axios from "axios";

const getFullBriefing = async () => {
    const response = await instance.get(`/api/briefs/all`);
    return response.data;
};

const getOneBriefing = async (briefId: string) => {
    console.log('axios info');
    console.log(axios);
    instance
    const response = await instance.get(`/api/briefs/${briefId}`);
    return response.data;
};

export {getFullBriefing, getOneBriefing};
