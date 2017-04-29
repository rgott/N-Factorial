using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace NFactorial
{
    public class MainWindowViewModel : ViewModelBase
    {
        public int NumberCountPerThread { get; set; } = 100;
        public int ThreadCount { get; set; } = 10;
        public int MaxElementQueueCount { get; set; } = 1000;
        public BigInteger FactorialInput { get; set; } = new BigInteger(123456);
        public BigInteger FactorialOutput { get; set; }
        public int FactorialOutputLength { get; set; }
        public long TimeTaken { get; set; }

        public string PartialProgressStatus { get; set; }
        public int PartialProgressValue { get; set; }
        public int PartialProgressMaximum { get; set; }

        public string TotalProgressStatus { get; set; }
        public int TotalProgressValue { get; set; }
        public int TotalProgressMaximum { get; set; }

        public ICommand CopyCmd { get; set; }
        public ICommand CancelCmd { get; set; }
        public ICommand ComputeCmd { get; set; }

        public MainWindowViewModel()
        {
            CopyCmd = new RelayCommand(() => { });
            CancelCmd = new RelayCommand(() => { });
            ComputeCmd = new RelayCommand(() => { });
        }
    }
}
