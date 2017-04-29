using System;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Collections.Concurrent;

namespace NFactorial
{
    public class MainWindowViewModel : ViewModelBase
    {
        public int NumberCountPerThread { get; set; } = 100;
        public int ThreadCount { get; set; } = 10;
        public int MaxElementQueueCount { get; set; } = 1000;
        public int FactorialInput { get; set; } = 5;
        public BigInteger FactorialOutput { get; set; }
        public int FactorialOutputLength { get; set; }
        public long TimeTaken { get; set; }

        private string _PartialProgressStatus;
        public string PartialProgressStatus
        {
            get
            {
                return _PartialProgressStatus;
            }
            set
            {
                _PartialProgressStatus = value;
                RaisePropertyChanged();
            }
        }
        public int PartialProgressValue { get; set; }
        public int PartialProgressMaximum { get; set; }

        private string _TotalProgressStatus;
        public string TotalProgressStatus
        {
            get
            {
                return _TotalProgressStatus;
            }
            set
            {
                _TotalProgressStatus = value;
                RaisePropertyChanged();
            }
        }
        public int TotalProgressValue { get; set; }
        public int TotalProgressMaximum { get; set; }

        public ICommand CopyCmd { get; set; }
        public ICommand CancelCmd { get; set; }
        public ICommand ComputeCmd { get; set; }


        private ConcurrentQueue<BigInteger> ThreadStorage = new ConcurrentQueue<BigInteger>();

        public MainWindowViewModel()
        {
            CopyCmd = new RelayCommand(Copy);
            CancelCmd = new RelayCommand(Cancel);
            ComputeCmd = new RelayCommand(Compute);
        }

        private void Copy()
        {
            throw new NotImplementedException();
        }

        private void Cancel()
        {
            throw new NotImplementedException();
        }

        private void Compute()
        {
            TotalProgressMaximum = FactorialInput;
            TotalProgressStatus = "STARTING";

            FactorialOutput = BigInteger.One;
            while (FactorialInput != 0)
            {
                FactorialOutput *= FactorialInput;
                FactorialInput -= 1;
                TotalProgressValue = FactorialInput;
            }
            TotalProgressStatus = "STOPPING";
        }
    }
}
