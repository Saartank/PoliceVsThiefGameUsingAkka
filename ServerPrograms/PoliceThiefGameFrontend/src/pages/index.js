import React, { useState } from "react";
import axios from "axios";
import GameDataTable from "@/components/GameDataTable";
import GraphImages from "@/components/GraphImages";
const MainComponent = ({ data }) => {
  let urlString = process.env.NEXT_PUBLIC_BACKEND_URL;
  const backendURL = urlString.endsWith("/")
    ? urlString.slice(0, -1)
    : urlString;
  const [node, setNode] = useState("");
  const [output, setOutput] = useState("");
  const [requestInProgress, setRequestInProgress] = useState(false);

  const [status, setStatus] = useState(data);

  const logAndSetOutput = (message) => {
    //console.log(message);
    setOutput(message);
  };

  const handleMovePolice = async () => {
    setRequestInProgress(true);
    try {
      logAndSetOutput("Sending request to Move Police...");
      const response = await axios.post(`${backendURL}/move/police/` + node);
      setStatus(response.data);
      logAndSetOutput(response.data);
    } catch (error) {
      console.error(error);
      logAndSetOutput("Error: " + error.message);
    } finally {
      setRequestInProgress(false);
    }
  };

  const handleMoveThief = async () => {
    setRequestInProgress(true);
    try {
      logAndSetOutput("Sending request to Move Thief...");
      const response = await axios.post(`${backendURL}/move/thief/` + node);
      setStatus(response.data);
      logAndSetOutput(response.data);
    } catch (error) {
      console.error(error);
      logAndSetOutput("Error: " + error.message);
    } finally {
      setRequestInProgress(false);
    }
  };

  const handleStatus = async () => {
    setRequestInProgress(true);
    try {
      logAndSetOutput("Sending request to Get Status...");
      const response = await axios.get(`${backendURL}/status`);
      setStatus(response.data);
      logAndSetOutput(response.data);
    } catch (error) {
      console.error(error);
      logAndSetOutput("Error: " + error.message);
    } finally {
      setRequestInProgress(false);
    }
  };

  const handleReset = async () => {
    setRequestInProgress(true);
    try {
      logAndSetOutput("Sending request to Reset...");
      const response = await axios.get(`${backendURL}/reset`);
      setStatus(response.data);
      logAndSetOutput(response.data);
    } catch (error) {
      console.error(error);
      logAndSetOutput("Error: " + error.message);
    } finally {
      setRequestInProgress(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="py-4 px-10 bg-gray-100">
        <h1 className="text-2xl font-semibold mb-4">Police Vs Thief Game!</h1>

        <div className="my-4">
          <label htmlFor="nodeInput" className="block font-medium">
            Node:
          </label>
          <input
            type="text"
            id="nodeInput"
            className="border rounded p-2 w-1/2"
            value={node}
            onChange={(e) => setNode(e.target.value)}
          />
        </div>

        <div className="my-4">
          <button
            className="bg-blue-500 text-white p-2 rounded mr-2"
            onClick={handleStatus}
          >
            Get Status
          </button>
          <button
            className="bg-blue-500 text-white p-2 rounded mr-2"
            onClick={handleMovePolice}
          >
            Move Police
          </button>
          <button
            className="bg-blue-500 text-white p-2 rounded mr-2"
            onClick={handleMoveThief}
          >
            Move Thief
          </button>

          <button
            className="bg-blue-500 text-white p-2 rounded"
            onClick={handleReset}
          >
            Reset
          </button>
        </div>

        <GameDataTable response={status} />

        <GraphImages />

        {requestInProgress && (
          <p className="text-blue-500 font-semibold">Request in Progress...</p>
        )}

        {output && (
          <div className="my-4">
            <h2 className="text-lg font-semibold">API Response:</h2>
            <pre className="border p-2 rounded">
              {JSON.stringify(output, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
};

export async function getServerSideProps(context) {
  try {
    // Make an API call using Axios
    let urlString = process.env.NEXT_PUBLIC_BACKEND_URL;
    const backendURL = urlString.endsWith("/")
      ? urlString.slice(0, -1)
      : urlString;

    //console.log("Sending request to Get Status...");
    const response = await axios.get(`${backendURL}/status`);
    console.log("Fetching current..");
    //console.log(response.data.message);

    // Return the data as props
    return {
      props: {
        data: response.data,
      },
    };
  } catch (error) {
    console.error("API call failed:", error);

    // Return empty data on error
    return {
      props: {
        data: null,
      },
    };
  }
}

export default MainComponent;
